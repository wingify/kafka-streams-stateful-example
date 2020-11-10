package com.wingify.example;

import com.wingify.example.models.Visitor;
import com.wingify.example.serdes.JsonSerializer;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;


public class TestDataGenerator {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final PropertiesConfiguration CONFIG = new PropertiesConfiguration();
    private static final String[] actions = {"click", "scroll"};
    private static final Integer[] customers = {1234, 5678};
    private static String KAFKA_TOPIC;
    private static String BOOTSTRAP_SERVERS;

    private static void loadProperties() throws Exception {
        CONFIG.load("application.properties");
        KAFKA_TOPIC = CONFIG.getString("kafka.topic");
        BOOTSTRAP_SERVERS = CONFIG.getString("kafka.bootstrap.servers");
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        loadProperties();
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);

        KafkaProducer<String, Visitor> producer = new KafkaProducer<>(props, new StringSerializer(), new JsonSerializer<>(Visitor.class));
        Random random = new Random();
        Integer numOfMessages = Integer.parseInt(args[0]);
        for (int i = 0; i < numOfMessages; i++){
            int index = Math.abs(random.nextInt() % 2);
            String userIdentifier = UUID.randomUUID().toString();
            Visitor visitor = new Visitor(customers[index], actions[index], userIdentifier);
            producer.send(new ProducerRecord<>(KAFKA_TOPIC, userIdentifier, visitor));
        }
        log.info("Sent {} messages", numOfMessages);
        producer.close();
    }

}
