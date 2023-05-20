package space.lobanov.translate;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class History {
    private String source;
    private String result;
    private Languages langTo;
    private Languages langFrom;
    private String UID;
    private long date;

    private static final String HISTORY_KEY = "History";
    private static final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(HISTORY_KEY);

    public History() {
    }
    public History(String source, Languages langTo, Languages langFrom, String UID, long date) {
        this.source = source;
        this.langTo = langTo;
        this.langFrom = langFrom;
        this.UID = UID;
        this.date = date;
    }

    public void insert(){
        mDatabase.push().setValue(this);
    }

    public static ArrayList<History> getElements() {
        ArrayList<History> elements = new ArrayList<>();
        Query query = mDatabase.orderByChild("uid").equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot children : snapshot.getChildren()){
                    History history = children.getValue(History.class);
                    System.out.println(history);
                    elements.add(history);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    @Override
    public String toString() {
        return "History{" +
                "source='" + source + '\'' +
                '}';
    }
}