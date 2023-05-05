package space.lobanov.translate;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import space.lobanov.translate.Database.DBHelper;
import space.lobanov.translate.Database.HistoryTable;

public class TranslationInfo {
    private long id;
    private Languages sourceLang;
    private Languages resultLang;
    private String source;
    private String result;
    private String date;

    public TranslationInfo(long id, Languages sourceLang, Languages resultLang, String source, String result, String date) {
        this.id = id;
        this.sourceLang = sourceLang;
        this.resultLang = resultLang;
        this.source = source;
        this.result = result;
        this.date = date;
    }

    public TranslationInfo(long id, Languages sourceLang, Languages resultLang, String source, String date) {
        this.id = id;
        this.sourceLang = sourceLang;
        this.resultLang = resultLang;
        this.source = source;
        this.date = date;
    }

    public void insert(){
        SQLiteDatabase database = DBHelper.database.getWritableDatabase();
        HistoryTable table = new HistoryTable();
        ContentValues values = new ContentValues();

        values.put(table.USER_ID, id);
        values.put(table.SOURCE_LANG, sourceLang.name());
        values.put(table.RESULT_LANG, resultLang.name());
        values.put(table.SOURCE, source);
        values.put(table.RESULT, result);
        values.put(table.DATE, date);

        database.insert(table.CONTACTS, null, values);
    }

    public static ArrayList<TranslationInfo> getElements(){
        HistoryTable table = new HistoryTable();
        ArrayList<TranslationInfo> elements = new ArrayList<>();

        SQLiteDatabase db = DBHelper.database.getReadableDatabase();
        Cursor cursor = db.query(table.CONTACTS, null, table.USER_ID + " = " + User.user.getId(), null, null, null, table.DATE + " DESC");

        while (cursor.moveToNext()){
            @SuppressLint("Range") long userId = cursor.getInt(cursor.getColumnIndex(table.USER_ID));
            @SuppressLint("Range") String sourceLang = cursor.getString(cursor.getColumnIndex(table.SOURCE_LANG));
            @SuppressLint("Range") String resultLang = cursor.getString(cursor.getColumnIndex(table.RESULT_LANG));
            @SuppressLint("Range") String source = cursor.getString(cursor.getColumnIndex(table.SOURCE));
            @SuppressLint("Range") String result = cursor.getString(cursor.getColumnIndex(table.RESULT));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(table.DATE));

            TranslationInfo item = new TranslationInfo(userId, Languages.valueOf(sourceLang), Languages.valueOf(resultLang), source, result, date);
            elements.add(item);
        }
        return elements;
    }

    public long getId() {
        return id;
    }

    public Languages getSourceLang() {
        return sourceLang;
    }

    public Languages getResultLang() {
        return resultLang;
    }

    public String getSource() {
        return source;
    }

    public String getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSourceLang(Languages sourceLang) {
        this.sourceLang = sourceLang;
    }

    public void setResultLang(Languages resultLang) {
        this.resultLang = resultLang;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
