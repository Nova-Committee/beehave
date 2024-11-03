package cool.muyucloud.beehave;

import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public final class Beehave {
    public static final String MOD_ID = "beehave";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Set<Item> VALID_ITEMS = new HashSet<>();
    public static final Config CONFIG = new Config();
    public static final Config DEFAULT_CONFIG = new Config();
    public static final TranslatorManager TRANSLATOR = new TranslatorManager("en_us", "zh_cn");

    public static void init() {
        LOGGER.info("Let's Beehave well!");
        LOGGER.info("Initializing config");
        CONFIG.load();
        LOGGER.info("Adding item filter");
        VALID_ITEMS.add(Items.AIR);
        // Register commands and server-starting/stopping events in fabric & neoforge modules
    }
}
