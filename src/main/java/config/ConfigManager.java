package config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    private ConfigManager() {}

    static {
        try (InputStream stream = ConfigManager.class.getClassLoader().getResourceAsStream(ConfigKeys.CONFIG_FILE)) {
            if (stream == null) {
                throw new RuntimeException("Config file not found: " + ConfigKeys.CONFIG_FILE);
            }

            PROPERTIES.load(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file: " + ConfigKeys.CONFIG_FILE, e);
        }
    }

    public static String get(String key) {
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String propertyValue = PROPERTIES.getProperty(key);

        if (propertyValue == null) {
            throw new RuntimeException("Missing configuration key: " + key);
        }

        return propertyValue;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
