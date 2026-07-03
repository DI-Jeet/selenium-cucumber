package com.sky.web.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.web.model.User;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserReader {

    private static final String USERS_FILE = "/config/users.json";
    private static final Map<String, User> cache = new HashMap<>();

    static {
        try (InputStream is = UserReader.class.getResourceAsStream(USERS_FILE)) {
            if (is == null) throw new IllegalStateException("users.json not found on classpath at " + USERS_FILE);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode users = root.get("users");
            users.fields().forEachRemaining(entry ->
                cache.put(entry.getKey(), mapper.convertValue(entry.getValue(), User.class))
            );
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private UserReader() {}

    public static User getUser(String key) {
        User user = cache.get(key);
        if (user == null) throw new IllegalArgumentException("No user found for key: " + key);
        return user;
    }
}
