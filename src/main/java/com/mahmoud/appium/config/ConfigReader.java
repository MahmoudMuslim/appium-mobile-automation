package com.mahmoud.appium.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads test configuration from {@code config.properties} (on the test classpath)
 * and lets any value be overridden by a JVM system property of the same name.
 *
 * <p>Example: {@code mvn test -DdeviceName=RF8M123ABCD} overrides the
 * {@code deviceName} entry in the properties file.</p>
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Could not find " + CONFIG_FILE + " on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + CONFIG_FILE, e);
        }
        return properties;
    }

    /** System property wins, otherwise the value from config.properties. */
    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return PROPERTIES.getProperty(key, "").trim();
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value.isEmpty() ? defaultValue : value;
    }
}
