package com.sky.web.hooks;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-scenario data bag injected by PicoContainer into every step definition class.
 * Provides a typed-key store for sharing state between step classes without static fields.
 */
public class ScenarioContext {

    public enum Key {
        SEARCH_TERM,
        SERIES_TITLE
    }

    private final Map<Key, Object> data = new HashMap<>();

    public void set(Key key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Key key) {
        return (T) data.get(key);
    }

    public boolean contains(Key key) {
        return data.containsKey(key);
    }
}
