package space.lobanov.translate.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import space.lobanov.translate.Adapters.LangItemsAdapter;
import space.lobanov.translate.Adapters.HistoryItemsAdapter;
import space.lobanov.translate.History;
import space.lobanov.translate.Languages;
import space.lobanov.translate.MainActivity;
import space.lobanov.translate.R;
import space.lobanov.translate.Translate;

public class HomeFragment extends Fragment implements TextView.OnEditorActionListener {

    private Translate mActivity;

    private LangItemsAdapter langAdapter;
    private EditText source;
    private TextView result;
    private ImageButton btnTranslate;
    private ImageButton btnReset;
    private ImageButton btnCopy;
    private ImageButton btnSwap;
    private Spinner spinnerLangFrom;
    private Spinner spinnerLangTo;
    private RecyclerView historyList;
    private HistoryItemsAdapter historyAdapter;
    private HomeFragment(){}

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
        setAdapters();
        setButtonsVisibility();

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

        btnTranslate.setOnClickListener(l -> getTranslation());

        Button button = mActivity.findViewById(R.id.button);
        button.setOnClickListener(l -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
            mActivity.finish();
        });

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int itemCount = historyAdapter.getItemCount();
                if (itemCount > 0) {
                    // Прокручиваем RecyclerView к последней позиции
                    historyList.scrollToPosition(itemCount - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().addValueEventListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        historyAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        historyAdapter.stopListening();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard();
            getTranslation();
        }
        return true;
    }

    private void getTranslation(){
        if (!source.getText().toString().trim().equals("")) {
            String text = source.getText().toString().trim();
            new Translator().execute(text);
        } else {
            Toast.makeText(mActivity, "Введите текст", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }

    private void connectResources(){
        source = mActivity.findViewById(R.id.source);
        result = mActivity.findViewById(R.id.result);
        btnTranslate = mActivity.findViewById(R.id.btnTranslate);
        btnReset = mActivity.findViewById(R.id.btnReset);
        btnCopy = mActivity.findViewById(R.id.btnCopy);
        btnSwap = mActivity.findViewById(R.id.btnSwap);
        historyList = mActivity.findViewById(R.id.historyList);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        historyList.setLayoutManager(manager);

        spinnerLangFrom = mActivity.findViewById(R.id.langFrom);
        spinnerLangTo = mActivity.findViewById(R.id.langTo);

        source.setHorizontallyScrolling(false);
        source.setMaxLines(6);
        source.setOnEditorActionListener(this);
    }

    public void setButtonsVisibility(){
        btnReset.setVisibility(View.INVISIBLE);
        btnCopy.setVisibility(View.INVISIBLE);

        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().equals("")){
                    btnReset.setVisibility(View.INVISIBLE);
                } else {
                    btnReset.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().equals("")){
                    btnCopy.setVisibility(View.INVISIBLE);
                } else {
                    btnCopy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setAdapters(){
        setLangAdapter();
        setHistoryAdapter();
    }

    private void setLangAdapter(){
        Languages[] values = Languages.values();
        langAdapter = new LangItemsAdapter(mActivity, R.layout.lang_spinner_title, R.layout.lang_spinner_dropdown, values);

        spinnerLangFrom.setAdapter(langAdapter);
        spinnerLangTo.setAdapter(langAdapter);

        Languages localLang = getLocale();

        spinnerLangFrom.setSelection(langAdapter.getPosition(Languages.English));
        spinnerLangTo.setSelection(langAdapter.getPosition(localLang));
    }

    private void setHistoryAdapter(){
        FirebaseRecyclerOptions<History> elements = History.getElements();
        historyAdapter = new HistoryItemsAdapter(elements);
        historyList.setAdapter(historyAdapter);
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


    private class Translator extends AsyncTask<String, String, String> {
        History item;
        @Override
        protected String doInBackground(String... strings) {
            item = getInfo(strings[0]);
            return executeRequest();
        }

        private History getInfo(String text){
            Languages langFrom = (Languages) spinnerLangFrom.getSelectedItem();
            Languages langTo = (Languages) spinnerLangTo.getSelectedItem();

            return new History(text, langTo, langFrom, FirebaseAuth.getInstance().getUid(), getUNIX());
        }

        private long getUNIX(){
            LocalDateTime localDateTime = LocalDateTime.now();
            Instant instant = Instant.parse(localDateTime + "Z");
            return instant.toEpochMilli();
        }

        private String executeRequest(){
            OkHttpClient client = new OkHttpClient();
            Response response;

            Request request = buildRequest();
            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Request buildRequest(){
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    String.format("{\"translateMode\":\"html\",\"platform\":\"api\"," +
                            "\"to\":\"%s\",\"from\":\"%s\",\"data\":\"%s\"}",
                            item.getLangTo().getCode(),
                            item.getLangFrom().getCode(), item.getSource()));
            return new Request.Builder()
                    .url("https://api-b2b.backenster.com/b1/api/v3/translate")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "a_fhvZYBPKemWAf00YLModU78yhoTduTE6gDzuibkunlUmch4GO9lOZp0DImFijwpdqKpGK8r3s8laNkoM")
                    .build();
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);

            String translation = parseJSON(json);
            result.setText(translation);

            item.setResult(translation);
            item.insert();
        }

        private String parseJSON(String s){
            try {
                JSONObject jsonObject = new JSONObject(s);
                return jsonObject.getString("result");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
