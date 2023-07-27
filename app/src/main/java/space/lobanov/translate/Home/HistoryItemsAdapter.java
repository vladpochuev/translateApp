package space.lobanov.translate.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import space.lobanov.translate.R;

public class HistoryItemsAdapter extends FirebaseRecyclerAdapter<HistoryItem,HistoryItemsAdapter.MyViewHolder> {
    public HistoryItemsAdapter(@NonNull FirebaseRecyclerOptions<HistoryItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull HistoryItem model) {
        String message = model.getLangFrom() + " ➞ "+ model.getLangTo();
        holder.languages.setText(message);
        holder.date.setText(getStringFromUNIX(model.getDate()));
        holder.source.setText(model.getSource());
        holder.result.setText(model.getResult());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(view);
    }

    private static String getStringFromUNIX(long unix){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Instant instant = Instant.ofEpochSecond(unix/1000);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dtf.format(localDateTime);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
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
