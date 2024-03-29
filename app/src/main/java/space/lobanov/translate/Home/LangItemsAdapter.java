package space.lobanov.translate.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import space.lobanov.translate.Languages;
import space.lobanov.translate.R;

public class LangItemsAdapter extends ArrayAdapter<Languages> {
    private final Context context;
    private final int mResource;
    private final int dropDownResource;
    private final Languages[] items;


    public LangItemsAdapter(@NonNull Context context, int mResource, int dropDownResource, @NonNull Languages[] items) {
        super(context, mResource, items);
        this.context = context;
        this.mResource = mResource;
        this.dropDownResource = dropDownResource;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(mResource, parent, false);
        }
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(items[position].name());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(dropDownResource, parent, false);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(items[position].name());
        return view;
    }
}
