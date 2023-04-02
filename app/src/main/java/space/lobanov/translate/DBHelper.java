package space.lobanov.translate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "translate";

    public static final String TABLE_CONTACTS = "users";
    public static final String KEY_ID = "_id";
    public static final String KEY_LOGIN = "_login";
    public static final String KEY_PASSWORD = "_password";

    public DBHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(DBHelperSQL.CREATE_TABLE.QUERY, TABLE_CONTACTS, KEY_ID,
                KEY_LOGIN, KEY_PASSWORD));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(String.format(DBHelperSQL.UPGRADE_TABLE.QUERY,TABLE_CONTACTS));
        onCreate(db);
    }

    public static DBHelper create(Context context){
        return new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);
    }

    enum DBHelperSQL{
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS " +
                "%s( %s INTEGER PRIMARY KEY AUTOINCREMENT," + // TABLE_CONTACTS, KEY_ID
                " %s VARCHAR(32)," + // KEY_LOGIN
                " %s VARCHAR(32) );"), // KEY_PASSWORD
        UPGRADE_TABLE("DROP TABLE IF EXISTS %s"); // TABLE_CONTACTS

        final String QUERY;
        DBHelperSQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
