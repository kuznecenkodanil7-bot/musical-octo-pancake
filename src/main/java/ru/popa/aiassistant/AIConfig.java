package ru.popa.aiassistant;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AIConfig {
    private static final String CONFIG_FILE_NAME = "aiassistant.properties";

    private static String apiKey = "";
    private static String endpoint = "https://api.openai.com/v1/responses";
    private static String model = "gpt-4.1-mini";
    private static int maxOutputTokens = 300;
    private static int timeoutSeconds = 45;

    private AIConfig() {
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        Properties properties = new Properties();

        if (Files.notExists(configPath)) {
            createDefaultConfig(configPath);
        }

        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            AIAssistantMod.LOGGER.error("Failed to read AI Assistant config", e);
        }

        apiKey = firstNotBlank(System.getenv("OPENAI_API_KEY"), properties.getProperty("openai.api_key", ""));
        endpoint = properties.getProperty("openai.endpoint", endpoint).trim();
        model = properties.getProperty("openai.model", model).trim();
        maxOutputTokens = parseInt(properties.getProperty("openai.max_output_tokens"), maxOutputTokens);
        timeoutSeconds = parseInt(properties.getProperty("openai.timeout_seconds"), timeoutSeconds);
    }

    private static void createDefaultConfig(Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            Properties defaults = new Properties();
            defaults.setProperty("openai.api_key", "PASTE_YOUR_OPENAI_API_KEY_HERE");
            defaults.setProperty("openai.endpoint", endpoint);
            defaults.setProperty("openai.model", model);
            defaults.setProperty("openai.max_output_tokens", String.valueOf(maxOutputTokens));
            defaults.setProperty("openai.timeout_seconds", String.valueOf(timeoutSeconds));

            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                defaults.store(outputStream, "AI Assistant config. You can also set OPENAI_API_KEY environment variable.");
            }
        } catch (IOException e) {
            AIAssistantMod.LOGGER.error("Failed to create default AI Assistant config", e);
        }
    }

    private static String firstNotBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        return second == null ? "" : second.trim();
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public static String apiKey() {
        return apiKey;
    }

    public static String endpoint() {
        return endpoint;
    }

    public static String model() {
        return model;
    }

    public static int maxOutputTokens() {
        return maxOutputTokens;
    }

    public static int timeoutSeconds() {
        return timeoutSeconds;
    }
}
