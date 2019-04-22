package com.volcano.visit.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcano.visit.reservation.exception.MemcachedFindException;
import com.volcano.visit.reservation.exception.MemcachedSaveException;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemcachedService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MemcachedClient mcc;
    @Value("${reservation.memcached.ip}")
    private String memcachedIp;

    @Value("${reservation.memcached.port}")
    private String memcachedPort;

    @PostConstruct
    public void setup() throws IOException {
        mcc = new MemcachedClient(new InetSocketAddress(memcachedIp, Integer.valueOf(memcachedPort)));
    }

    OperationFuture set(final String key, final int exp, final Object value) {
        String jsonValue;
        jsonValue = serializeObject(key, value);
        return mcc.set(key, exp, jsonValue);
    }

    Object get(final String key, final Class clazz) {
        String jsonValue = (String) mcc.get(key);
        return deserializeObject(key, jsonValue, clazz);
    }

    OperationFuture delete(final String key) {
        return mcc.delete(key);
    }

    public OperationFuture flush() {
        return mcc.flush();
    }

    private String serializeObject(final String key, final Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new MemcachedSaveException("Failed to save " + key + " into cache", e);
        }
    }

    private Object deserializeObject(final String key, final String jsonValue, final Class clazz) {
        if (jsonValue != null) {
            try {
                return objectMapper.readValue(jsonValue, clazz);
            } catch (IOException e) {
                throw new MemcachedFindException("Failed to find " + key + " from cache", e);
            }
        }
        return null;
    }
}
