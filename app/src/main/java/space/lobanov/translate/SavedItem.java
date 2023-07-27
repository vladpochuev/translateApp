package space.lobanov.translate;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedItem implements Insertable {
    private String source;
    private String result;
    private Languages langTo;
    private Languages langFrom;
    private String UID;
    private static final String KEY = "SavedItem";
    public static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(KEY);

    public SavedItem() {}

    public SavedItem(String source, String result, Languages langTo, Languages langFrom, String UID) {
        this.source = source;
        this.result = result;
        this.langTo = langTo;
        this.langFrom = langFrom;
        this.UID = UID;
    }

    @Override
    public void insert() {
        mDatabase.push().setValue(this);
    }

    public static FirebaseRecyclerOptions<SavedItem> getRecycleOptions(){
        Query query = mDatabase.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid());
        return new FirebaseRecyclerOptions.Builder<SavedItem>()
                .setQuery(query, SavedItem.class).build();
    }

    public static void getElements(FirebaseCallback callback) {
        Query query = mDatabase.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<SavedItem> elements = new ArrayList<>();
                for (DataSnapshot children : snapshot.getChildren()){
                    SavedItem item = children.getValue(SavedItem.class);
                    elements.add(item);
                }
                callback.onDataLoaded(elements);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Languages getLangTo() {
        return langTo;
    }

    public void setLangTo(Languages langTo) {
        this.langTo = langTo;
    }

    public Languages getLangFrom() {
        return langFrom;
    }

    public void setLangFrom(Languages langFrom) {
        this.langFrom = langFrom;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
