package space.lobanov.translate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import space.lobanov.translate.TranslationInfo;
import space.lobanov.translate.R;

public class HistoryItemsAdapter extends ArrayAdapter<TranslationInfo> {

    private Context context;
    private List<TranslationInfo> items;

    public HistoryItemsAdapter(Context context, ArrayList<TranslationInfo> items) {
        super(context, R.layout.item, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);

        TranslationInfo item = items.get(position);

        TextView languages = view.findViewById(R.id.itemLang);
        TextView date = view.findViewById(R.id.itemDate);
        TextView source = view.findViewById(R.id.itemSource);
        TextView result = view.findViewById(R.id.itemResult);

        languages.setText(item.getSourceLang() + " ➞ "+ item.getResultLang());
        date.setText(item.getDate());
        source.setText(item.getSource());
        result.setText(item.getResult());

        return view;
    }
}
