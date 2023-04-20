package space.lobanov.translate;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private Spinner spinnerLangFrom;
    private Spinner spinnerLangTo;
    private ListView historyList;

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
        historyList = findViewById(R.id.historyList);

        spinnerLangFrom = findViewById(R.id.langFrom);
        spinnerLangTo = findViewById(R.id.langTo);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setAdapters();

        
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
            Languages leftSpinner = (Languages) spinnerLangFrom.getSelectedItem();
            Languages rightSpinner = (Languages) spinnerLangTo.getSelectedItem();

            spinnerLangFrom.setSelection(langAdapter.getPosition(rightSpinner));
            spinnerLangTo.setSelection(langAdapter.getPosition(leftSpinner));
        });

        btnTranslate.setOnClickListener(l -> {
            String text = source.getText().toString().trim();
            new GetURLData().execute(text);
        });
    }

    private void setAdapters(){
        setLangAdapter();
        setHistoryAdapter();
    }

    private void setLangAdapter(){
        Languages[] values = Languages.values();
        langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLangFrom.setAdapter(langAdapter);
        spinnerLangTo.setAdapter(langAdapter);

        Languages localLang = getLocale();

        spinnerLangFrom.setSelection(langAdapter.getPosition(Languages.English));
        spinnerLangTo.setSelection(langAdapter.getPosition(localLang));
    }

    private void setHistoryAdapter(){
        ItemAdapter itemAdapter = new ItemAdapter(this, HistoryItem.getElements());
        historyList.setAdapter(itemAdapter);
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
            Languages langTo = (Languages) spinnerLangTo.getSelectedItem();
            Languages langFrom = (Languages) spinnerLangFrom.getSelectedItem();

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
                JSONObject jsonObject = new JSONObject(s);
                String translate = jsonObject.getString("result");
                result.setText(translate);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Languages langSource = (Languages) spinnerLangFrom.getSelectedItem();
            Languages langResult = (Languages) spinnerLangTo.getSelectedItem();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            HistoryItem item = new HistoryItem(User.user.getId(), langSource.name(),
                    langResult.name(), source.getText().toString().trim(),
                    result.getText().toString().trim(), dateFormat.format(date));

            item.insert();

            setHistoryAdapter();
        }
    }
}
