package space.lobanov.translate;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LanguageCollector {
    private ArrayList<Languages> enabledLanguages;

    private static final int MIN_NUMBER_OF_ITEMS = 10;

    public LanguageCollector(ArrayList<SavedItem> allItems) {
        enabledLanguages = getEnabledLanguages(allItems);
    }

    private ArrayList<Languages> getEnabledLanguages(ArrayList<SavedItem> allItems) {
        HashMap<Languages, Integer> languages = new HashMap<>();
        for (SavedItem item : allItems) {
            Languages lang = item.getLangTo();
            languages.put(lang, languages.getOrDefault(lang, 0) + 1);
        }
        ArrayList<Languages> enabledLanguages = new ArrayList<>();
        for (Map.Entry<Languages, Integer> entry : languages.entrySet()) {
            if (entry.getValue() >= MIN_NUMBER_OF_ITEMS) {
                enabledLanguages.add(entry.getKey());
            }
        }
        return enabledLanguages;
    }
    public Languages pickLanguage() {
        double ran =  Math.random();
        int randomIndex = (int) (ran * (enabledLanguages.size() - 1));
        return enabledLanguages.get(randomIndex);
    }
}
