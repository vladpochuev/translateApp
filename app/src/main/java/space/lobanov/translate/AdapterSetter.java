package space.lobanov.translate;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

import space.lobanov.translate.Home.HistoryItem;
import space.lobanov.translate.Home.HistoryItemsAdapter;
import space.lobanov.translate.Home.LangItemsAdapter;

public class AdapterSetter {
    private final Context context;

    public AdapterSetter(Context context) {
        this.context = context;
    }

    public LangItemsAdapter getLangAdapter(){
        Languages[] values = Languages.values();
        return new LangItemsAdapter(context, R.layout.lang_spinner_title, R.layout.lang_spinner_dropdown, values);
    }

    public Languages getLocale() {
        Locale current = Locale.getDefault();
        String curCode = current.getLanguage() + "_" + current.getCountry();

        for (Languages language : Languages.values()) {
            if(curCode.equals(language.getCode())){
                return language;
            }
        }
        return Languages.Ukrainian;
    }

    public HistoryItemsAdapter getHistoryAdapter() {
        FirebaseRecyclerOptions<HistoryItem> elements = HistoryItem.getElements();
        return new HistoryItemsAdapter(elements);
    }

    public SavedItemsAdapter getSavedAdapter() {
        FirebaseRecyclerOptions<SavedItem> elements = SavedItem.getRecycleOptions();
        return new SavedItemsAdapter(elements);
    }
}
