package ru.popa.aiassistant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class OpenAIClient {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private OpenAIClient() {
    }

    public static String ask(String question) {
        if (AIConfig.apiKey().isBlank() || AIConfig.apiKey().equals("PASTE_YOUR_OPENAI_API_KEY_HERE")) {
            return "API-ключ не настроен. Открой config/aiassistant.properties и вставь openai.api_key, либо задай переменную OPENAI_API_KEY.";
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", AIConfig.model());
        requestBody.add("input", createInput(question));
        requestBody.addProperty("max_output_tokens", AIConfig.maxOutputTokens());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AIConfig.endpoint()))
                .timeout(Duration.ofSeconds(AIConfig.timeoutSeconds()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AIConfig.apiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return "OpenAI вернул HTTP " + response.statusCode() + ": " + shorten(response.body(), 500);
            }

            return extractText(response.body());
        } catch (IOException e) {
            AIAssistantMod.LOGGER.error("Network error while calling OpenAI", e);
            return "Сетевая ошибка при обращении к OpenAI: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Запрос к OpenAI был прерван.";
        } catch (Exception e) {
            AIAssistantMod.LOGGER.error("Unexpected error while calling OpenAI", e);
            return "Ошибка обработки ответа OpenAI: " + e.getMessage();
        }
    }

    private static JsonArray createInput(String question) {
        JsonArray input = new JsonArray();

        JsonObject system = new JsonObject();
        system.addProperty("role", "system");
        system.addProperty("content", "Ты ИИ-ассистент внутри Minecraft. Отвечай по-русски, кратко и понятно. Не выдавай очень длинные ответы.");
        input.add(system);

        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", question);
        input.add(user);

        return input;
    }

    private static String extractText(String responseBody) {
        JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();

        if (root.has("output_text") && !root.get("output_text").isJsonNull()) {
            String text = root.get("output_text").getAsString();
            if (!text.isBlank()) {
                return text.trim();
            }
        }

        StringBuilder result = new StringBuilder();
        JsonArray output = root.has("output") && root.get("output").isJsonArray()
                ? root.getAsJsonArray("output")
                : new JsonArray();

        for (JsonElement outputElement : output) {
            if (!outputElement.isJsonObject()) {
                continue;
            }

            JsonObject outputObject = outputElement.getAsJsonObject();
            if (!outputObject.has("content") || !outputObject.get("content").isJsonArray()) {
                continue;
            }

            for (JsonElement contentElement : outputObject.getAsJsonArray("content")) {
                if (!contentElement.isJsonObject()) {
                    continue;
                }

                JsonObject contentObject = contentElement.getAsJsonObject();
                if (contentObject.has("text") && !contentObject.get("text").isJsonNull()) {
                    result.append(contentObject.get("text").getAsString());
                }
            }
        }

        String text = result.toString().trim();
        return text.isEmpty() ? "Не удалось найти текст ответа в JSON." : text;
    }

    private static String shorten(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String clean = text.replace('\n', ' ').replace('\r', ' ').trim();
        return clean.length() <= maxLength ? clean : clean.substring(0, maxLength) + "...";
    }
}
