package translate;

import base.BaseTranslate;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Translate extends BaseTranslate {

    @Test
    public void translateSingleSentenceToAllLanguages() {
        // Load the first sentence from file
        String sentence = readFromJsonFile(
                "src/test/resources/translate/text2translate.json",
                new TypeReference<List<String>>() {}
        ).get(0);

        // Use preloaded language list from BaseTranslate
        List<String> languages = expectedLanguages;

        // Store translations for each language
        Map<String, String> translations = new HashMap<>();

        for (String lang : languages) {
            String translatedText = translate(sentence, lang);

            // Validate translation
            assertNotNull(translatedText, "Translation is null for lang: " + lang);
            assertFalse(translatedText.isEmpty(), "Translation is empty for lang: " + lang);
            if (!lang.equalsIgnoreCase("en")) {
                assertNotEquals(sentence, translatedText, "Translation same as source for lang: " + lang);
            }

            translations.put(lang, translatedText);
        }

        // Save all translations to JSON
        writeJsonToFile(translations, "src/test/resources/translate/translatedText.json");

        // Final check: all languages processed
        assertEquals(languages.size(), translations.size(), "Some languages are missing from the output.");

        System.out.println("✅ Translations written to: src/test/resources/translate/translatedText.json");
    }
}