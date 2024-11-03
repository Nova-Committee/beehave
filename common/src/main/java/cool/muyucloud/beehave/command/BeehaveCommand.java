package cool.muyucloud.beehave.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class BeehaveCommand {
    private static final Config CONFIG = Beehave.CONFIG;
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("beehave");
        root.requires(serverCommandSource -> serverCommandSource.hasPermission(2));
        root.executes(context -> showAll(context.getSource()));
        PropertySubCommand.register(root);
        ConfigFileSubCommand.register(root);
        dispatcher.register(root);
    }

    private static int showAll(CommandSourceStack source) {
        MutableComponent text = TRANSLATOR.translate("message.command.beehave.title");
        for (String key : CONFIG.getProperties()) {
            text.append("\n- ")
                .append(getStyledKey(key))
                .append(": ")
                .append(CONFIG.getAsString(key))
                .append(" ");
        }
        source.sendSuccess(() -> text, false);
        return 1;
    }

    private static MutableComponent getStyledKey(String key) {
        MutableComponent info = TRANSLATOR.translate("message.command.beehave.info.%s".formatted(key));
        String suggest = "/beehave %s ".formatted(key);
        return Component.literal(key).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN)
            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, info)));
    }
}
