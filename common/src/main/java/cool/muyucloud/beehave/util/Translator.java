package cool.muyucloud.beehave.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cool.muyucloud.beehave.Beehave;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Translator {
    private final String langName;
    private final HashMap<String, String> mappings;
    private boolean bad = false;

    public Translator(String langName) {
        this.langName = langName;
        this.mappings = this.readLangFile();
    }

    public String getLangName() {
        return langName;
    }

    public MutableComponent translate(String key, Object... args) {
        if (mappings.containsKey(key)) {
            return Component.literal((mappings.get(key).formatted(args)));
        } else {
            return Component.literal(key);
        }
    }

    public boolean isBad() {
        return this.bad;
    }

    private HashMap<String, String> readLangFile() {
        String json = "{}";
        try {
            json = IOUtils.toString(
                Objects.requireNonNull(
                    Translator.class.getClassLoader().getResource(
                        "assets/beehave/lang/%s.json"
                            .formatted(this.langName))),
                StandardCharsets.UTF_8);
        } catch (Exception e) {
            Beehave.LOGGER.error("target language file %s not present".formatted(this.langName));
            this.bad = true;
        }
        return new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {}.getType());
    }
}
