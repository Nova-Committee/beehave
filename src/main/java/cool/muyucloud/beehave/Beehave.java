package cool.muyucloud.beehave;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class Beehave implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final HashSet<Item> INVALID_ITEMS = new HashSet<>();

    @Override
    public void onInitialize() {
        INVALID_ITEMS.add(Items.GLASS_BOTTLE);
        INVALID_ITEMS.add(Items.SHEARS);
    }
}
