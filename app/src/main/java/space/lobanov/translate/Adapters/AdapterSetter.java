package space.lobanov.translate.Adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

import space.lobanov.translate.HistoryItem;
import space.lobanov.translate.Languages;
import space.lobanov.translate.R;
import space.lobanov.translate.SavedItem;
import space.lobanov.translate.SavedItemsAdapter;

public class AdapterSetter {
    private Context context;

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
        FirebaseRecyclerOptions<SavedItem> elements = SavedItem.getElements();
        return new SavedItemsAdapter(elements);
    }
}
