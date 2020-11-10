package com.wingify.example.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDeserializer<T> implements Deserializer<T> {
    private static final Logger log = LoggerFactory.getLogger(JsonDeserializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> deserializedClass;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        T data;
        try {
            data = objectMapper.readValue(bytes, deserializedClass);
        } catch (Exception e) {
            log.error("Failed to Deserialize : {}", e);
            return null;
        }

        return data;
    }

}
