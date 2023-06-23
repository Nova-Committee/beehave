package cool.muyucloud.beehave.util;

import cool.muyucloud.beehave.Beehave;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TranslatorManager {
    public static final TranslatorManager INSTANCE = new TranslatorManager();
    private static final Logger LOGGER = Beehave.LOGGER;

    private final HashMap<String, Translator> translators = new HashMap<>();
    private String currentLang = "en_us";
    private Translator translator;

    public TranslatorManager(String... langNames) {
        this.translator = new Translator("en_us");
        this.translators.put("en_us", this.translator);
        for (String langName : langNames) {
            if (Objects.equals(langName, "en_us")) {
                continue;
            }
            Translator t = new Translator(langName);
            if (t.isBad()) {
                continue;
            }
            this.translators.put(langName, t);
        }
    }

    public String getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(String currentLang) {
        if (this.translators.containsKey(currentLang)) {
            this.currentLang = currentLang;
            this.translator = translators.get(currentLang);
        } else {
            LOGGER.warn("Target language is not loaded");
        }
    }

    public List<String> validLangs() {
        return this.translators.keySet().stream().toList();
    }

    public MutableText translate(String key, Object... args) {
        return this.translator.translate(key, args);
    }
}
