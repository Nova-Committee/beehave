package cool.muyucloud.beehave.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class ConfigFileSubCommand {
    private static final Config CONFIG = Beehave.CONFIG;
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;

    public static void register(LiteralArgumentBuilder<CommandSourceStack> parent) {
        LiteralArgumentBuilder<CommandSourceStack> nodeLoad = Commands.literal("load");
        nodeLoad.executes(context -> load(context.getSource()));
        LiteralArgumentBuilder<CommandSourceStack> nodeSave = Commands.literal("save");
        nodeSave.executes(context -> save(context.getSource()));
    }

    private static int load(CommandSourceStack source) {
        if (CONFIG.load()) {
            MutableComponent text = TRANSLATOR.translate("message.command.beehave.load.success");
            source.sendSuccess(() -> text, false);
            return 1;
        } else {
            MutableComponent text = TRANSLATOR.translate("message.command.beehave.load.fail");
            source.sendFailure(text);
            return 0;
        }
    }

    private static int save(CommandSourceStack source) {
        if (CONFIG.save()) {
            MutableComponent text = TRANSLATOR.translate("message.command.beehave.save.success");
            source.sendSuccess(() -> text, false);
            return 1;
        } else {
            MutableComponent text = TRANSLATOR.translate("message.command.beehave.save.fail");
            source.sendFailure(text);
            return 0;
        }
    }
}
