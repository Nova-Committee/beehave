package cool.muyucloud.beehave.fabric;

import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.command.BeehaveCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class BeehaveFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Beehave.init();
        Beehave.LOGGER.info("Registering command");
        Event<CommandRegistrationCallback> event = CommandRegistrationCallback.EVENT;
        event.register((dispatcher, registryAccess, environment) -> BeehaveCommand.register(dispatcher));
        Beehave.LOGGER.info("Registering lifecycle events");
        ServerLifecycleEvents.SERVER_STARTING.register(server -> Beehave.CONFIG.load());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Beehave.CONFIG.save());
    }
}
