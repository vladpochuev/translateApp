package space.lobanov.translate;

import java.util.ArrayList;

public interface FirebaseCallback {
    void onDataLoaded(ArrayList<SavedItem> items);
}
