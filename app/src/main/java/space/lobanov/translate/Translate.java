package space.lobanov.translate;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Translate extends AppCompatActivity {
    private EditText source;
    private TextView result;
    private Button btnTranslate;
    private Button btnReset;
    private Button btnCopy;
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

        btnTranslate.setOnClickListener(l -> {
            String text = source.getText().toString().trim();
            new GetURLData().execute(text);
        });
    }

    private void setAdapter(){
        Languages[] values = Languages.values();
        ArrayAdapter<Languages> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spLangFrom.setAdapter(langAdapter);
        spLangTo.setAdapter(langAdapter);
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
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
