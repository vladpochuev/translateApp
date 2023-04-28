package space.lobanov.translate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class HomeFragment extends Fragment {

    private Translate mActivity;

    ArrayAdapter<Languages> langAdapter;
    private EditText source;
    private TextView result;
    private ImageButton btnTranslate;
    private ImageButton btnReset;
    private ImageButton btnCopy;
    private ImageButton btnSwap;
    private Spinner spinnerLangFrom;
    private Spinner spinnerLangTo;
    private ListView historyList;
    private HomeFragment(){

    }

    public static HomeFragment newInstance(){
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (Translate) getActivity();

        connectResources();

        Window w = mActivity.getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// - скрыть нижнюю панель навигации
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//-нижняя панель будет появляться только при вызове и исчезать через несколько секунд

        setAdapters();


        btnReset.setOnClickListener(l -> {
            source.setText("");
            result.setText("");
        });

        btnCopy.setOnClickListener(l -> {
            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", result.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mActivity.getApplicationContext(), "Текст скопирован в буфер обмена", Toast.LENGTH_SHORT).show();
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


    private void connectResources(){
        source = mActivity.findViewById(R.id.source);
        result = mActivity.findViewById(R.id.result);
        btnTranslate = mActivity.findViewById(R.id.btnTranslate);
        btnReset = mActivity.findViewById(R.id.btnReset);
        btnCopy = mActivity.findViewById(R.id.btnCopy);
        btnSwap = mActivity.findViewById(R.id.btnSwap);
        historyList = mActivity.findViewById(R.id.historyList);

        spinnerLangFrom = mActivity.findViewById(R.id.langFrom);
        spinnerLangTo = mActivity.findViewById(R.id.langTo);
    }

    private void setAdapters(){
        setLangAdapter();
        setHistoryAdapter();
    }

    private void setLangAdapter(){
        Languages[] values = Languages.values();
        langAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, values);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLangFrom.setAdapter(langAdapter);
        spinnerLangTo.setAdapter(langAdapter);

        Languages localLang = getLocale();

        spinnerLangFrom.setSelection(langAdapter.getPosition(Languages.English));
        spinnerLangTo.setSelection(langAdapter.getPosition(localLang));
    }

    private void setHistoryAdapter(){
        HistoryItemsAdapter historyItemsAdapter = new HistoryItemsAdapter(mActivity, HistoryItem.getElements());
        historyList.setAdapter(historyItemsAdapter);
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

    private class GetURLData extends AsyncTask<String, String, String> {
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
