package com.wingify.example.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wingify.example.models.Visitor;
import com.wingify.example.models.VisitorAggregated;
import com.wingify.example.serdes.JsonDeserializer;
import com.wingify.example.serdes.JsonSerializer;
import com.wingify.example.processors.VisitorProcessor;

import java.util.Properties;

import static com.wingify.example.Main.*;


public class StreamsProcessor {
    private static final Logger log = LoggerFactory.getLogger(StreamsProcessor.class);

    /**
     * Starts the Kafka Streams application
     */
    public void run() {
        Properties streamsConfig = new Properties();
        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        Serde<Visitor> visitorSerde = Serdes.serdeFrom(
                new JsonSerializer<>(Visitor.class),
                new JsonDeserializer<>(Visitor.class));

        Serde<VisitorAggregated> visitorAggregatedSerde = Serdes.serdeFrom(
                new JsonSerializer<>(VisitorAggregated.class),
                new JsonDeserializer<>(VisitorAggregated.class));

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(AGGREGATE_KV_STORE_ID),
                        Serdes.Integer(), visitorAggregatedSerde
                ).withLoggingDisabled().withCachingDisabled());

        streamsBuilder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore(COUNT_KV_STORE_ID),
                        Serdes.Integer(), Serdes.Integer()
                ).withLoggingDisabled().withCachingDisabled());

        streamsBuilder
                .stream(KAFKA_TOPIC, Consumed.with(Serdes.String(), visitorSerde))
                .filter((k, v) -> v != null)
                .mapValues(VisitorAggregated::new)
                .transform(() -> new VisitorProcessor(AGGREGATE_THRESHOLD, AGGREGATE_DURATION),
                        AGGREGATE_KV_STORE_ID, COUNT_KV_STORE_ID)
                .foreach((k, v) -> writeToSink(k, v));

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsConfig);
        log.info("starting kafka streams");
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    /**
     * Placeholder method for simulating a database write operation
     * @param key
     * @param value
     */
    private void writeToSink(String key, VisitorAggregated value) {
        //Placeholder method for simulating a write operation to an external sink.
        log.info("Persisting to Sink : {} {}", key, value);
    }

}
