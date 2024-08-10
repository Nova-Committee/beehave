package cool.muyucloud.beehave;

import cool.muyucloud.beehave.command.BeehaveCommand;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class Beehave implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final HashSet<Item> VALID_ITEMS = new HashSet<>();
    public static final Config CONFIG = new Config();
    public static final Config DEFAULT_CONFIG = new Config();
    public static final TranslatorManager TRANSLATOR = new TranslatorManager("en_us", "zh_cn");

    @Override
    public void onInitialize() {
        LOGGER.info("Let's Beehave well!");
        LOGGER.info("Initializing config");
        CONFIG.load();
        LOGGER.info("Adding item filter");
        VALID_ITEMS.add(Items.AIR);
        LOGGER.info("Registering command");
        Event<CommandRegistrationCallback> event = CommandRegistrationCallback.EVENT;
        event.register((dispatcher, registryAccess, environment) -> BeehaveCommand.register(dispatcher));
        LOGGER.info("Registering lifecycle events");
        ServerLifecycleEvents.SERVER_STARTING.register(server -> CONFIG.load());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CONFIG.save());
    }
}