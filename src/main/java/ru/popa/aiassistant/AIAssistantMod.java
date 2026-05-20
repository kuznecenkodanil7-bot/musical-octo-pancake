package ru.popa.aiassistant;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AIAssistantMod implements ModInitializer {
    public static final String MOD_ID = "aiassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AIConfig.load();
        AICommand.register();
        LOGGER.info("AI Assistant mod initialized");
    }
}
