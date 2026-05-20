package ru.popa.aiassistant;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public final class AICommand {
    private AICommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("askai")
                        .then(CommandManager.argument("question", StringArgumentType.greedyString())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    String question = StringArgumentType.getString(context, "question").trim();

                                    if (question.isEmpty()) {
                                        source.sendFeedback(() -> Text.literal("[AI] Напиши вопрос после команды: /askai <вопрос>"), false);
                                        return 0;
                                    }

                                    source.sendFeedback(() -> Text.literal("[AI] Думаю..."), false);

                                    CompletableFuture
                                            .supplyAsync(() -> OpenAIClient.ask(question))
                                            .thenAccept(answer -> source.getServer().execute(() -> sendLongMessage(source, "[AI] ", answer)))
                                            .exceptionally(error -> {
                                                source.getServer().execute(() -> source.sendFeedback(
                                                        () -> Text.literal("[AI] Ошибка: " + error.getMessage()),
                                                        false
                                                ));
                                                AIAssistantMod.LOGGER.error("AI request failed", error);
                                                return null;
                                            });

                                    return 1;
                                })
                        )
        ));
    }

    private static void sendLongMessage(ServerCommandSource source, String prefix, String message) {
        String clean = message == null || message.isBlank() ? "Пустой ответ." : message.trim();
        int maxPartLength = 240;

        if (clean.length() <= maxPartLength) {
            source.sendFeedback(() -> Text.literal(prefix + clean), false);
            return;
        }

        for (int start = 0; start < clean.length(); start += maxPartLength) {
            int end = Math.min(start + maxPartLength, clean.length());
            String part = clean.substring(start, end);
            source.sendFeedback(() -> Text.literal(prefix + part), false);
        }
    }
}
