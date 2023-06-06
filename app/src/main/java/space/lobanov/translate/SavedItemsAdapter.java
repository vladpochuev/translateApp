package space.lobanov.translate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SavedItemsAdapter extends FirebaseRecyclerAdapter<SavedItem, SavedItemsAdapter.MyViewHolder> {

    public SavedItemsAdapter(@NonNull FirebaseRecyclerOptions<SavedItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SavedItem model) {
        holder.tvLang.setText(model.getLangFrom() + " ➞ "+ model.getLangTo());
        holder.tvSource.setText(model.getSource());
        holder.tvResult.setText(model.getResult());

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_item, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLang, tvSource, tvResult;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLang = itemView.findViewById(R.id.tvLang);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvResult = itemView.findViewById(R.id.tvResult);
        }
    }
}
