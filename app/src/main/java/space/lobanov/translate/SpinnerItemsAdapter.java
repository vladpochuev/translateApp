package space.lobanov.translate;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class SpinnerItemsAdapter extends ArrayAdapter<String> {

    public SpinnerItemsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
