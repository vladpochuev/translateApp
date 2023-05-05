package space.lobanov.translate.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper database;
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "translate";

    public DBHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new UsersTable().getCreateQuery());
        db.execSQL(new HistoryTable().getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(new UsersTable().getUpdateQuery());
        db.execSQL(new HistoryTable().getUpdateQuery());
        onCreate(db);
    }

    public static DBHelper create(Context context){
        return new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);
    }
}
