package space.lobanov.translate;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoryItem implements Insertable {
    private String source;
    private String result;
    private Languages langTo;
    private Languages langFrom;
    private String UID;
    private long date;

    private static final String KEY = "HistoryItem";
    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(KEY);

    public HistoryItem() {}
    public HistoryItem(String source, Languages langTo, Languages langFrom, String UID, long date) {
        this.source = source;
        this.langTo = langTo;
        this.langFrom = langFrom;
        this.UID = UID;
        this.date = date;
    }
    @Override
    public void insert(){
        mDatabase.push().setValue(this);
    }

    public static FirebaseRecyclerOptions<HistoryItem> getElements(){
        Query query = mDatabase.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid());
        FirebaseRecyclerOptions<HistoryItem> elements = new FirebaseRecyclerOptions.Builder<HistoryItem>()
                .setQuery(query, HistoryItem.class).build();
        return elements;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
