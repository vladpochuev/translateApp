package space.lobanov.translate;

import static space.lobanov.translate.DBHelper.UsersTable.*;
import static space.lobanov.translate.DBHelper.HistoryTable.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class DBHelper extends SQLiteOpenHelper {
    static DBHelper database;
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "translate";

    public DBHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(DBHelperSQL.CREATE_USERS_TABLE.QUERY, USERS_TABLE_CONTACTS,
                USERS_KEY_ID, USERS_KEY_LOGIN, USERS_KEY_PASSWORD));

        db.execSQL(String.format(DBHelperSQL.CREATE_HISTORY_TABLE.QUERY, HISTORY_TABLE_CONTACTS,
                HISTORY_USER_ID, HISTORY_SOURCE_LANG, HISTORY_RESULT_LANG, HISTORY_SOURCE,
                HISTORY_RESULT, HISTORY_DATE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(String.format(DBHelperSQL.UPGRADE_USERS_TABLE.QUERY, USERS_TABLE_CONTACTS));
        db.execSQL(String.format(DBHelperSQL.UPGRADE_HISTORY_TABLE.QUERY, HISTORY_TABLE_CONTACTS));
        onCreate(db);
    }

    public static DBHelper create(Context context){
        return new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);
    }

    public static class UsersTable {
        public static final String USERS_TABLE_CONTACTS = "users";
        public static final String USERS_KEY_ID = "_id";
        public static final String USERS_KEY_LOGIN = "_login";
        public static final String USERS_KEY_PASSWORD = "_password";
    }

    public static class HistoryTable {
        public static final String HISTORY_TABLE_CONTACTS = "history";

        public static final String HISTORY_USER_ID = "_user_id";
        public static final String HISTORY_SOURCE_LANG = "_source_lang";
        public static final String HISTORY_RESULT_LANG = "_result_lang";
        public static final String HISTORY_SOURCE = "_source";
        public static final String HISTORY_RESULT = "_result";
        public static final String HISTORY_DATE = "_date";
    }

    enum DBHelperSQL{
        CREATE_USERS_TABLE("CREATE TABLE IF NOT EXISTS " +
                "%s( %s INTEGER PRIMARY KEY AUTOINCREMENT," + // USERS_TABLE_CONTACTS, USERS_KEY_ID
                " %s VARCHAR(32)," + // USERS_KEY_LOGIN
                " %s VARCHAR(32) );"), // USERS_KEY_PASSWORD
        UPGRADE_USERS_TABLE("DROP TABLE IF EXISTS %s"), // USERS_TABLE_CONTACTS

        CREATE_HISTORY_TABLE("CREATE TABLE IF NOT EXISTS " +
                "%s(%s INTEGER, " + // HISTORY_TABLE_CONTACTS, HISTORY_USER_ID
                "%s VARCHAR(32), " +  //  HISTORY_SOURCE_LANG
                "%s VARCHAR(32), " + // HISTORY_RESULT_LANG
                "%s VARCHAR(256), " + // HISTORY_SOURCE
                "%s VARCHAR(256), " + // HISTORY_RESULT
                "%s VARCHAR(40) );"), // HISTORY_DATE
        UPGRADE_HISTORY_TABLE("DROP TABLE IF EXISTS %s"); // HISTORY_TABLE_CONTACTS
        final String QUERY;
        DBHelperSQL(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
