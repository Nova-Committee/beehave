package cool.muyucloud.beehave.util;

import com.google.gson.JsonPrimitive;
import cool.muyucloud.beehave.Beehave;
import cool.muyucloud.beehave.config.Config;
import net.minecraft.text.MutableText;

import java.util.HashMap;
import java.util.Objects;

public class TranslatorManager {
    public static final Config CONFIG = Beehave.CONFIG;

    private final HashMap<String, Translator> translators = new HashMap<>();
    private Translator translator;
    private Translator defaultTranslator;

    public TranslatorManager(String... langNames) {
        for (String langName : langNames) {
            Translator t = new Translator(langName);
            if (t.isBad()) {
                continue;
            }
            this.translators.put(langName, t);
        }
        this.initDefault();
        this.updateLang();
    }

    private void initDefault() {
        Translator t = this.translators.get("en_us");
        if (t == null) {
            t = new Translator("en_us");
            this.translators.put("en_us", t);
        }
        this.defaultTranslator = t;
        this.translator = t;
    }

    public String getCurrentLang() {
        return this.translator.getLangName();
    }

    public MutableText translate(String key, Object... args) {
        this.updateLang();
        return this.translator.translate(key, args);
    }

    private void updateLang() {
        String loaded = this.translator.getLangName();
        String current = CONFIG.getAsString("lang");
        if (!Objects.equals(loaded, current)) {
            Translator t = this.translators.get(current);
            this.translator = t == null ? defaultTranslator : t;
            CONFIG.set("lang", new JsonPrimitive(this.getCurrentLang()));
        }
    }
}
