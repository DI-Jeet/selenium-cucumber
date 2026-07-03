package com.sky.web.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton config loader. Reads config/{env}.properties at startup.
 * Priority (highest first): -Dkey=value → env var → properties file.
 * Select env with -Denv=qa (default: qa).
 */
public class ConfigLoader {

    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    private static ConfigLoader instance;
    private final Properties properties = new Properties();

    private ConfigLoader() {
        String env = System.getProperty("env", "qa");
        String filePath = "config/" + env + ".properties";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalStateException("Config file not found on classpath: " + filePath);
            }
            properties.load(is);
            log.info("Loaded config for environment: {}", env);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config: " + filePath, e);
        }
    }

    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    /**
     * Returns the value for the given key. Lookup order:
     * 1. JVM system property (-Dkey=value)
     * 2. OS environment variable (KEY_WITH_UNDERSCORES uppercased)
     * 3. Properties file
     */
    public String get(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;

        String envVar = System.getenv(key.toUpperCase().replace(".", "_"));
        if (envVar != null && !envVar.isBlank()) return envVar;

        String value = properties.getProperty(key);
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            // Unresolved placeholder — try environment variable for the placeholder name
            String placeholder = value.substring(2, value.length() - 1);
            String resolved = System.getenv(placeholder);
            if (resolved != null) return resolved;
            log.warn("Config key '{}' has unresolved placeholder: {}", key, value);
            return null;
        }
        return value;
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null) ? value : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return (value != null) ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid integer for key '{}': '{}', using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }
}
