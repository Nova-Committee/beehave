package cool.muyucloud.beehave.command;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import cool.muyucloud.beehave.util.TranslatorManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class PropertySubCommand {
    private static final Config CONFIG = Beehave.CONFIG;
    private static final Config DEFAULT_CONFIG = Beehave.DEFAULT_CONFIG;
    private static final TranslatorManager TRANSLATOR = Beehave.TRANSLATOR;

    public static void register(LiteralArgumentBuilder<ServerCommandSource> parent) {
        for (String key : DEFAULT_CONFIG.getProperties()) {
            LiteralArgumentBuilder<ServerCommandSource> nodeKey;
            nodeKey = CommandManager.literal(key);
            nodeKey.executes(context -> get(context.getSource(), key));
            Class<? extends Serializable> type = DEFAULT_CONFIG.getType(key);
            RequiredArgumentBuilder<ServerCommandSource, ? extends Serializable> nodeValue = genNodeValue(key, type);
            LiteralArgumentBuilder<ServerCommandSource> nodeDefault = genNodeDefault(key, type);
            nodeKey.then(nodeValue);
            nodeKey.then(nodeDefault);
            parent.then(nodeKey);
        }
    }

    @NotNull
    private static LiteralArgumentBuilder<ServerCommandSource> genNodeDefault(String key, Class<? extends Serializable> type) {
        LiteralArgumentBuilder<ServerCommandSource> nodeDefault =
            CommandManager.literal("default");
        nodeDefault.executes(context -> {
            ServerCommandSource source = context.getSource();
            JsonPrimitive value = primitiveProperty(key, type);
            return set(source, key, value);
        });
        return nodeDefault;
    }

    private static JsonPrimitive primitiveProperty(String key, Class<? extends Serializable> type) {
        if (type == Number.class) {
            Integer value = DEFAULT_CONFIG.getAsInt(key);
            return new JsonPrimitive(value);
        } else if (type == Boolean.class) {
            Boolean value = DEFAULT_CONFIG.getAsBoolean(key);
            return new JsonPrimitive(value);
        } else if (type == String.class) {
            String value = DEFAULT_CONFIG.getAsString(key);
            return new JsonPrimitive(value);
        }
        return null;
    }

    @NotNull
    private static RequiredArgumentBuilder<ServerCommandSource, ? extends Serializable> genNodeValue(String key, Class<? extends Serializable> type) {
        ArgumentType<? extends Serializable> argumentType = argumentType(type);
        RequiredArgumentBuilder<ServerCommandSource, ? extends Serializable> nodeValue =
            CommandManager.argument("key", argumentType);
        nodeValue.executes(context -> {
            ServerCommandSource source = context.getSource();
            JsonPrimitive value = readArgument(type, context, "key");
            return set(source, key, value);
        });
        return nodeValue;
    }

    private static ArgumentType<? extends Serializable> argumentType(Class<? extends Serializable> type) {
        if (type == Number.class) {
            return IntegerArgumentType.integer();
        } else if (type == Boolean.class) {
            return BoolArgumentType.bool();
        } else if (type == String.class) {
            return StringArgumentType.string();
        }
        return null;
    }

    private static JsonPrimitive readArgument(Class<? extends Serializable> type, CommandContext<ServerCommandSource> context, String name) {
        if (type == Number.class) {
            Integer value = IntegerArgumentType.getInteger(context, name);
            return new JsonPrimitive(value);
        } else if (type == Boolean.class) {
            Boolean value = BoolArgumentType.getBool(context, name);
            return new JsonPrimitive(value);
        } else if (type == String.class) {
            String value = StringArgumentType.getString(context, name);
            return new JsonPrimitive(value);
        }
        return null;
    }

    private static int set(ServerCommandSource source, String key, JsonPrimitive value) {
        if (CONFIG.set(key, value))  {
            MutableText text = TRANSLATOR.translate("message.command.beehave.property.set.success", key, value.getAsString());
            source.sendFeedback(text, false);
            return 1;
        } else {
            MutableText text = TRANSLATOR.translate("message.command.beehave.property.set.fail", key, value.getAsString());
            source.sendError(text);
            return 0;
        }
    }

    private static int get(ServerCommandSource source, String key) {
        String value = CONFIG.getAsString(key);
        if (value != null) {
            MutableText text = TRANSLATOR.translate("message.command.beehave.property.get.success", key, value);
            source.sendFeedback(text, false);
            return 1;
        } else {
            MutableText text = TRANSLATOR.translate("message.command.beehave.property.get.failure", key);
            source.sendError(text);
            return 0;
        }
    }
}
