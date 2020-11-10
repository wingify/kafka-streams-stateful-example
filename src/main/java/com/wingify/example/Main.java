package com.wingify.example;

import com.wingify.example.streams.StreamsProcessor;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


public class Main {
    public static final PropertiesConfiguration PROPERTIES = new PropertiesConfiguration();
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static String KAFKA_TOPIC;
    public static String APPLICATION_ID;
    public static Integer COMMIT_INTERVAL;
    public static Duration AGGREGATE_DURATION;
    public static Integer AGGREGATE_THRESHOLD;
    public static String AGGREGATE_KV_STORE_ID;
    public static String COUNT_KV_STORE_ID;
    public static String BOOTSTRAP_SERVERS;

    /**
     * Read and set config params from the application.properties file
     * @throws Exception
     */
    private static void loadProperties() throws Exception {
        PROPERTIES.load("application.properties");
        KAFKA_TOPIC = PROPERTIES.getString("kafka.topic");
        APPLICATION_ID = PROPERTIES.getString("application.id");
        AGGREGATE_DURATION = Duration.ofMillis(Integer.parseInt(PROPERTIES.getString("aggregation.duration.ms")));
        COMMIT_INTERVAL = Integer.parseInt(PROPERTIES.getString("aggregation.commit.interval.ms"));
        AGGREGATE_THRESHOLD = Integer.parseInt(PROPERTIES.getString("aggregation.threshold"));
        AGGREGATE_KV_STORE_ID = PROPERTIES.getString("aggregation.kv.store.id");
        COUNT_KV_STORE_ID = PROPERTIES.getString("aggregation.count.store.id");
        BOOTSTRAP_SERVERS = PROPERTIES.getString("kafka.bootstrap.servers");
    }

    public static void main(String[] args) {
        try {
            loadProperties();
            StreamsProcessor streamsProcessor = new StreamsProcessor();
            streamsProcessor.run();
            // For running the DSL `groupByKey` + `reduce` operators solution
            //StreamsDSL streamsDSL = new StreamsDSL();
            //streamsDSL.run();
        } catch (Exception e) {
            log.error("Failed to Start Kafka Streams : {}", e);
        }

    }
}
