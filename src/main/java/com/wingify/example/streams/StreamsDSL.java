package com.wingify.example.streams;

import com.wingify.example.Main;
import com.wingify.example.serdes.JsonDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wingify.example.models.Visitor;
import com.wingify.example.models.VisitorAggregated;
import com.wingify.example.serdes.JsonSerializer;

import java.time.Duration;
import java.util.Properties;

public class StreamsDSL {

    private static final Logger log = LoggerFactory.getLogger(StreamsDSL.class);

    /**
     * Starts the Kafka Streams application
     */
    public void run() {
        Properties streamsConfig = new Properties();
        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, Main.APPLICATION_ID);
        streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Main.BOOTSTRAP_SERVERS);
        streamsConfig.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Main.COMMIT_INTERVAL);

        Serde<Visitor> visitorSerde = Serdes.serdeFrom(
                new JsonSerializer<>(Visitor.class),
                new JsonDeserializer<>(Visitor.class));

        Serde<VisitorAggregated> visitorAggregatedSerde = Serdes.serdeFrom(
                new JsonSerializer<>(VisitorAggregated.class),
                new JsonDeserializer<>(VisitorAggregated.class));

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        Duration windowDuration = Duration.ofMillis(Main.COMMIT_INTERVAL);
        TimeWindows window = TimeWindows.of(windowDuration).advanceBy(windowDuration);

        streamsBuilder
                .stream(Main.KAFKA_TOPIC, Consumed.with(Serdes.String(), visitorSerde))
                .filter((k, v) -> v != null)
                .map((k, v) -> KeyValue.pair(v.getCustomerId(), new VisitorAggregated(v)))
                .groupByKey(Grouped.with((Serdes.Integer()), visitorAggregatedSerde))
                .windowedBy(window.grace(Duration.ZERO))
                .reduce(VisitorAggregated::merge)
                .suppress(Suppressed.untilTimeLimit(windowDuration, Suppressed.BufferConfig.unbounded()))
                .toStream()
                .foreach((k, v) -> writeToSink(k.toString(), v));

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
        log.info("Persisting to Sink : {} {}", key, value);
    }
}
