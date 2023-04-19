package space.lobanov.translate;

import static space.lobanov.translate.DBHelper.HistoryTable.*;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Translate extends AppCompatActivity {

    ArrayAdapter<Languages> langAdapter;
    private EditText source;
    private TextView result;
    private Button btnTranslate;
    private Button btnReset;
    private Button btnCopy;
    private Button btnSwap;
    private Spinner spLangFrom;
    private Spinner spLangTo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        source = findViewById(R.id.source);
        result = findViewById(R.id.result);
        btnTranslate = findViewById(R.id.btnTranslate);
        btnReset = findViewById(R.id.btnReset);
        btnCopy = findViewById(R.id.btnCopy);
        btnSwap = findViewById(R.id.btnSwap);

        spLangFrom = findViewById(R.id.langFrom);
        spLangTo = findViewById(R.id.langTo);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setAdapter();
        
        btnReset.setOnClickListener(l -> {
            source.setText("");
            result.setText("");
        });

        btnCopy.setOnClickListener(l -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", result.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Текст скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
        });

        btnSwap.setOnClickListener(l -> {
            Languages leftSpinner = (Languages) spLangFrom.getSelectedItem();
            Languages rightSpinner = (Languages) spLangTo.getSelectedItem();

            spLangFrom.setSelection(langAdapter.getPosition(rightSpinner));
            spLangTo.setSelection(langAdapter.getPosition(leftSpinner));
        });

        btnTranslate.setOnClickListener(l -> {
            String text = source.getText().toString().trim();
            new GetURLData().execute(text);
        });
    }

    private void setAdapter(){
        Languages[] values = Languages.values();
        langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLangFrom.setAdapter(langAdapter);
        spLangTo.setAdapter(langAdapter);

        Languages localLang = getLocale();

        spLangFrom.setSelection(langAdapter.getPosition(Languages.English));
        spLangTo.setSelection(langAdapter.getPosition(localLang));
    }

    private Languages getLocale() {
        Locale current = Locale.getDefault();
        String curCode = current.getLanguage() + "_" + current.getCountry();

        for (Languages language : Languages.values()) {
            if(curCode.equals(language.getCode())){
                return language;
            }
        }
        return Languages.Ukrainian;
    }

    private class GetURLData extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            String text = strings[0];
            Languages langTo = (Languages) spLangTo.getSelectedItem();
            Languages langFrom = (Languages) spLangFrom.getSelectedItem();

            OkHttpClient client = new OkHttpClient();
            Response response;

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, String.format("{\"translateMode\":\"html\",\"platform\":\"api\",\"to\":\"%s\",\"from\":\"%s\",\"data\":\"%s\"}", langTo.getCode(), langFrom.getCode(),  text));
            Request request = new Request.Builder()
                    .url("https://api-b2b.backenster.com/b1/api/v3/translate")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "a_fhvZYBPKemWAf00YLModU78yhoTduTE6gDzuibkunlUmch4GO9lOZp0DImFijwpdqKpGK8r3s8laNkoM")
                    .build();
            try {
                response = client.newCall(request).execute();
                System.out.println("Access");
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                System.out.println(s);
                JSONObject jsonObject = new JSONObject(s);
                String translate = jsonObject.getString("result");
                result.setText(translate);
                System.out.println(result.getText());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            SQLiteDatabase database = DBHelper.database.getWritableDatabase();
            ContentValues values = new ContentValues();
            Languages langSource = (Languages) spLangFrom.getSelectedItem();
            Languages langResult = (Languages) spLangTo.getSelectedItem();

            values.put(HISTORY_USER_ID, User.user.getId());
            values.put(HISTORY_SOURCE_LANG, langSource.name());
            values.put(HISTORY_RESULT_LANG, langResult.name());
            values.put(HISTORY_SOURCE, source.getText().toString().trim());
            values.put(HISTORY_RESULT, result.getText().toString().trim());
            values.put(HISTORY_DATE, new Date().toString());

            System.out.println(result.getText() + " ");

            database.insert(HISTORY_TABLE_CONTACTS, null, values);

            SQLiteDatabase db = DBHelper.database.getReadableDatabase();
            Cursor cursor = db.query(HISTORY_TABLE_CONTACTS, null, null, null, null, null, null);

            while (cursor.moveToNext()){
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(HISTORY_USER_ID));
                @SuppressLint("Range") String sourceLang = cursor.getString(cursor.getColumnIndex(HISTORY_SOURCE_LANG));
                @SuppressLint("Range") String resultLang = cursor.getString(cursor.getColumnIndex(HISTORY_RESULT_LANG));
                @SuppressLint("Range") String source = cursor.getString(cursor.getColumnIndex(HISTORY_SOURCE));
                @SuppressLint("Range") String result = cursor.getString(cursor.getColumnIndex(HISTORY_RESULT));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(HISTORY_DATE));

                System.out.printf("Id : %d, SouceLang : %s, ResultLang : %s, Source : %s, Result : %s, Date : %s\n", userId, sourceLang, resultLang, source, result, date);
            }
        }
    }
}
