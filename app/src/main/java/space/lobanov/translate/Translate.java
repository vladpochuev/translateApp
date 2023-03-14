package space.lobanov.translate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        source = findViewById(R.id.source);
        result = findViewById(R.id.result);
        button = findViewById(R.id.button);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        button.setOnClickListener(l -> {
            String text = source.getText().toString().trim();
            new GetURLData().execute(text);
        });
    }
    private class GetURLData extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            String text = strings[0];

            OkHttpClient client = new OkHttpClient();
            Response response;

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, String.format("{\"translateMode\":\"html\",\"platform\":\"api\",\"to\":\"ru_RU\",\"from\":\"en_GB\",\"data\":\"%s\"}", text));
            Request request = new Request.Builder()
                    .url("https://api-b2b.backenster.com/b1/api/v3/translate")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "a_fhvZYBPKemWAf00YLModU78yhoTduTE6gDzuibkunlUmch4GO9lOZp0DImFijwpdqKpGK8r3s8laNkoM")
                    .build();
            try {
                response = client.newCall(request).execute();
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
        }
    }
}
