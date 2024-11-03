package cool.muyucloud.beehave.neoforge;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.command.BeehaveCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.command.NeoForgeCommand;

@Mod(Beehave.MOD_ID)
public final class BeehaveNeoForge {
    public BeehaveNeoForge() {
        Beehave.init();
        Beehave.LOGGER.info("Registering events");
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void onRegisterCommands(RegisterCommandsEvent event) {
        Beehave.LOGGER.info("Registering command");
        BeehaveCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    private void onServerStarting(ServerStartingEvent event) {
        Beehave.CONFIG.load();
    }

    @SubscribeEvent
    private void onServerStopping(ServerStoppingEvent event) {
        Beehave.CONFIG.save();
    }
}
