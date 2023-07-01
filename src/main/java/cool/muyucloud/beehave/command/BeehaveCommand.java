package cool.muyucloud.beehave.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class BeehaveCommand {
    private static final Config CONFIG = Beehave.CONFIG;
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        ArgumentBuilder<?, ?> builder;
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("beehave");
        root.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        root.executes(context -> showAll(context.getSource()));
        PropertySubCommand.register(root);
        ConfigFileSubCommand.register(root);
        dispatcher.register(root);
    }

    private static int showAll(ServerCommandSource source) {
        MutableText text = TRANSLATOR.translate("message.command.beehave.title");
        for (String key : CONFIG.getProperties()) {
            text.append("\n- ")
                .append(getStyledKey(key))
                .append(": ")
                .append(CONFIG.getAsString(key))
                .append(" ");
        }
        source.sendFeedback(text, false);
        return 1;
    }

    private static MutableText getStyledKey(String key) {
        String.format("message.command.beehave.info.%s", key);
        MutableText info = TRANSLATOR.translate("message.command.beehave.info.%s".formatted(key));
        String suggest = "/beehave %s ".formatted(key);
        return Text.literal(key).setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GREEN)
            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, info)));
    }
}
