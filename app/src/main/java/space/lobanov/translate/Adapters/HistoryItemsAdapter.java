package space.lobanov.translate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import space.lobanov.translate.History;
import space.lobanov.translate.R;

public class HistoryItemsAdapter extends ArrayAdapter<History> {

    private Context context;
    private List<History> items;

    public HistoryItemsAdapter(Context context, ArrayList<History> items) {
        super(context, R.layout.item, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);

        History item = items.get(position);

        TextView languages = view.findViewById(R.id.itemLang);
        TextView date = view.findViewById(R.id.itemDate);
        TextView source = view.findViewById(R.id.itemSource);
        TextView result = view.findViewById(R.id.itemResult);

        languages.setText(item.getLangFrom() + " ➞ "+ item.getLangTo());
        date.setText(getStringFromUNIX(item.getDate()));
        source.setText(item.getSource());
        result.setText(item.getResult());

        return view;
    }

    private static String getStringFromUNIX(long unix){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Instant instant = Instant.ofEpochSecond(unix/1000);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }
}
