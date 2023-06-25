package cool.muyucloud.beehave.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;

public class ConfigFileSubCommand {
    private static final Config CONFIG = Beehave.CONFIG;
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;

    public static void register(LiteralArgumentBuilder<ServerCommandSource> parent) {
        LiteralArgumentBuilder<ServerCommandSource> nodeLoad = CommandManager.literal("load");
        nodeLoad.executes(context -> load(context.getSource()));
        LiteralArgumentBuilder<ServerCommandSource> nodeSave = CommandManager.literal("save");
        nodeSave.executes(context -> save(context.getSource()));
//        parent.then(nodeLoad);
//        parent.then(nodeSave);
    }

    private static int load(ServerCommandSource source) {
        if (CONFIG.load()) {
            MutableText text = TRANSLATOR.translate("message.command.beehave.load.success");
            source.sendFeedback(text, false);
            return 1;
        } else {
            MutableText text = TRANSLATOR.translate("message.command.beehave.load.fail");
            source.sendError(text);
            return 0;
        }
    }

    private static int save(ServerCommandSource source) {
        if (CONFIG.save()) {
            MutableText text = TRANSLATOR.translate("message.command.beehave.save.success");
            source.sendFeedback(text, false);
            return 1;
        } else {
            MutableText text = TRANSLATOR.translate("message.command.beehave.save.fail");
            source.sendError(text);
            return 0;
        }
    }
}
