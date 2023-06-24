package committee.nova.beehave;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

@Mod(Beehave.MODID)
public class Beehave {
    public static final String MODID = "beehave";

    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.BooleanValue bee;
    public static final ForgeConfigSpec.BooleanValue beehive;

    static {
        final var builder = new ForgeConfigSpec.Builder();
        builder.comment("Beehave Settings").push("general");
        // TODO: Comment
        bee = builder.define("bee", true);
        beehive = builder.define("beehive", true);
        builder.pop();
        CFG = builder.build();
    }

    public Beehave() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
