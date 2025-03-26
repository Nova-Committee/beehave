package cool.muyucloud.beehave.forge;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.command.BeehaveCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Beehave.MOD_ID)
public final class BeehaveForge {
    public BeehaveForge() {
        Beehave.init();
        Beehave.LOGGER.info("Registering events");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Beehave.LOGGER.info("Registering command");
        BeehaveCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        Beehave.CONFIG.load();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        Beehave.CONFIG.save();
    }
}
