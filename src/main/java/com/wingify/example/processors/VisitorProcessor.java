package com.wingify.example.processors;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import com.wingify.example.Main;
import com.wingify.example.models.VisitorAggregated;

import java.time.Duration;

public class VisitorProcessor implements Transformer<String, VisitorAggregated, KeyValue<String, VisitorAggregated>>, Punctuator {
    private final Duration interval;
    private ProcessorContext ctx;
    private KeyValueStore<Integer, VisitorAggregated> aggregateStore;
    private KeyValueStore<Integer, Integer> countStore;
    private final Integer threshold;

    public VisitorProcessor(Integer threshold, Duration interval) {
        this.threshold = threshold;
        this.interval = interval;
    }

    public void init(ProcessorContext context) {
        this.ctx = context;
        this.aggregateStore = (KeyValueStore) context.getStateStore(Main.AGGREGATE_KV_STORE_ID);
        this.countStore = (KeyValueStore) context.getStateStore(Main.COUNT_KV_STORE_ID);
        this.ctx.schedule(interval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
    }

    @Override
    public KeyValue<String, VisitorAggregated> transform(String key, VisitorAggregated visitor) {
        KeyValue<String, VisitorAggregated> toForward = null;

        Integer stateStoreKey = visitor.getCustomerId();
        countStore.putIfAbsent(stateStoreKey, 0);
        aggregateStore.putIfAbsent(stateStoreKey, new VisitorAggregated(visitor.getCustomerId()));

        Integer aggregateCount = countStore.get(stateStoreKey) + 1;
        VisitorAggregated visitorAggregated = visitor.merge(aggregateStore.get(stateStoreKey));
        aggregateStore.put(stateStoreKey, visitorAggregated);
        countStore.put(stateStoreKey, aggregateCount);

        if (aggregateCount >= threshold) {
            toForward = KeyValue.pair(key, visitorAggregated);
            countStore.delete(stateStoreKey);
            aggregateStore.delete(stateStoreKey);
        }
        return toForward;
    }

    private void forwardAll() {
        KeyValueIterator<Integer, VisitorAggregated> it = aggregateStore.all();
        while (it.hasNext()) {
            KeyValue<Integer, VisitorAggregated> entry = it.next();
            ctx.forward(String.valueOf(entry.key), entry.value);
            aggregateStore.delete(entry.key);
            countStore.delete(entry.key);
        }
        it.close();
    }

    @Override
    public void punctuate(long timestamp) {
        forwardAll();
    }

    @Override
    public void close() {
        forwardAll();
    }
}
