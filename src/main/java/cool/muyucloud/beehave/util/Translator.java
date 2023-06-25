package cool.muyucloud.beehave.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cool.muyucloud.beehave.Beehave;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

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

    public MutableText translate(String key, Object... args) {
        if (mappings.containsKey(key)) {
            return new LiteralText(String.format(mappings.get(key), args));
        } else {
            return new LiteralText(key);
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
                        String.format("assets/beehave/lang/%s.json",
                            this.langName))),
                StandardCharsets.UTF_8);
        } catch (Exception e) {
            Beehave.LOGGER.error(String.format("target language file %s not present", this.langName));
            this.bad = true;
        }
        return new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {}.getType());
    }
}
