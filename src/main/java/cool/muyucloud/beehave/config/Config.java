package cool.muyucloud.beehave.config;

import com.google.gson.*;
import cool.muyucloud.beehave.Beehave;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Config {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("beehave.json");
    private static final Logger LOGGER = Beehave.LOGGER;

    private final JsonObject content;

    public Config() {
        this.content = new JsonObject();
        this.content.addProperty("beehive", true);
        this.content.addProperty("bee", true);
//        this.content.addProperty("whitelist", false);
        this.content.addProperty("lang", "en_us");
//        JsonArray blacklist = new JsonArray();
//        blacklist.add(Registries.ITEM.getId(Items.SHEARS).toString());
//        blacklist.add(Registries.ITEM.getId(Items.GLASS_BOTTLE).toString());
//        this.content.add("blacklist", blacklist);
//        this.content.add("whitelist", new JsonArray());
    }

    private static Config createAndLoad() {
        Config config = new Config();
        config.load();
        return config;
    }

    public boolean load() {
        if (!tryDeployFile()) {
            return false;
        }
        this.readFile();
        this.dump();
        return true;
    }

    public boolean save() {
        if (!tryDeployFile()) {
            return false;
        }
        this.dump();
        return true;
    }

    private void dump() {
        String json = (new GsonBuilder().setPrettyPrinting().create()).toJson(this.content);
        try (OutputStream outputStream = Files.newOutputStream(CONFIG_PATH)) {
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error("Cannot dump settings to file");
            e.printStackTrace();
        }
    }

    public Set<String> getProperties() {
        HashSet<String> out = new HashSet<>();
        for (String key : this.content.keySet()) {
            if (this.content.get(key).isJsonArray()) {
                continue;
            }
            out.add(key);
        }
        return out;
    }

    public Class<? extends Serializable> getType(String key) {
        if (!this.content.has(key)) {
            throw new NullPointerException("Tried to access property %s but it does not exists!".formatted(key));
        }
        JsonPrimitive primitive = this.content.getAsJsonPrimitive(key);
        if (primitive.isBoolean()) {
            return Boolean.class;
        } else if (primitive.isNumber()) {
            return Number.class;
        } else if (primitive.isString()) {
            return String.class;
        }
        return null;
    }

    public String getAsString(String key) {
        if (!this.content.has(key)) {
            return null;
        }
        return this.content.getAsJsonPrimitive(key).getAsString();
    }

    public Boolean getAsBoolean(String key) {
        JsonPrimitive primitive = this.content.getAsJsonPrimitive(key);
        return primitive == null ? null :
            primitive.isBoolean() ? primitive.getAsBoolean() : null;
    }

    public Integer getAsInt(String key) {
        JsonPrimitive primitive = this.content.getAsJsonPrimitive(key);
        return primitive == null ? null :
            primitive.isNumber() ? primitive.getAsInt() : null;
    }

    public boolean set(String key, JsonPrimitive value) {
        if (!this.content.has(key)) {
            return false;
        }

        JsonPrimitive dst = this.content.getAsJsonPrimitive(key);
        if (value.isNumber() && !dst.isNumber()) {
            return false;
        } else if (value.isBoolean() && !dst.isBoolean()) {
            return false;
        } else if (value.isString() && !dst.isString()) {
            return false;
        }

        this.content.add(key, value);
        return true;
    }


    private static boolean tryDeployFile() {
        if (!Files.exists(CONFIG_PATH)) {
            // try to create new config file
            LOGGER.info("beehave.json does not exist, generating.");
            try {
                Files.createFile(CONFIG_PATH);
            } catch (Exception e) {
                LOGGER.error("Failed to generate config file at %s.".formatted(CONFIG_PATH));
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void readFile() {
        try (InputStream stream = Files.newInputStream(CONFIG_PATH)) {
            JsonObject object = (new Gson()).fromJson(
                new String(stream.readAllBytes(), StandardCharsets.UTF_8),
                JsonObject.class
            );
            this.readConfig(object);
        } catch (Exception e) {
            LOGGER.error("Failed to load config from file");
        }
    }

    private void readConfig(JsonObject object) {
        try {
            this.readProperties(object);
        } catch (Exception e) {
            LOGGER.error("Failed to load config from file");
        }
    }

    private void readProperties(JsonObject object) {
        for (String key : object.keySet()) {
            if (this.content.has(key)) {
                JsonPrimitive dst = this.content.getAsJsonPrimitive(key);
                JsonPrimitive src = object.getAsJsonPrimitive(key);
                if (dst.isBoolean()) {
                    this.content.addProperty(key, src.getAsBoolean());
                } else if (dst.isNumber()) {
                    this.content.addProperty(key, src.getAsNumber());
                } else if (dst.isString()) {
                    this.content.addProperty(key, src.getAsString());
                } else {
                    this.readItemArray(key, object);
                }
            }
        }
    }

    private void readItemArray(String key, JsonObject object) {
        this.content.remove(key);
        JsonArray dst = this.content.getAsJsonArray(key);
        JsonArray src = object.getAsJsonArray(key);
        for (JsonElement element : src) {
            Identifier id = Identifier.of(element.getAsString());
            if (Registries.ITEM.containsId(id)) {
                dst.add(id.toString());
            }
        }
    }
}
