package space.lobanov.translate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import space.lobanov.translate.History;
import space.lobanov.translate.R;

public class HistoryItemsAdapter extends FirebaseRecyclerAdapter<History,HistoryItemsAdapter.MyViewHolder> {
    public HistoryItemsAdapter(@NonNull FirebaseRecyclerOptions<History> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull History model) {
        holder.languages.setText(model.getLangFrom() + " ➞ "+ model.getLangTo());
        holder.date.setText(getStringFromUNIX(model.getDate()));
        holder.source.setText(model.getSource());
        holder.result.setText(model.getResult());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    private static String getStringFromUNIX(long unix){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Instant instant = Instant.ofEpochSecond(unix/1000);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView languages;
        TextView date;
        TextView source;
        TextView result;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            languages = itemView.findViewById(R.id.itemLang);
            date = itemView.findViewById(R.id.itemDate);
            source = itemView.findViewById(R.id.itemSource);
            result = itemView.findViewById(R.id.itemResult);
        }

    }
}
