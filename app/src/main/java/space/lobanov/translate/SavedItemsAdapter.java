package space.lobanov.translate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

public class SavedItemsAdapter extends FirebaseRecyclerAdapter<SavedItem, SavedItemsAdapter.MyViewHolder> {

    public SavedItemsAdapter(@NonNull FirebaseRecyclerOptions<SavedItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SavedItem model) {
        String message = model.getLangFrom() + " ➞ "+ model.getLangTo();
        holder.tvLang.setText(message);
        holder.tvSource.setText(model.getSource());
        holder.tvResult.setText(model.getResult());
        holder.bDelete.setOnClickListener(l -> {
            DatabaseReference itemRef = getRef(position);
            itemRef.removeValue();
        });

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_item, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLang, tvSource, tvResult;
        ImageButton bDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLang = itemView.findViewById(R.id.tvLang);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvResult = itemView.findViewById(R.id.tvResult);
            bDelete = itemView.findViewById(R.id.bDelete);
        }
    }
}
