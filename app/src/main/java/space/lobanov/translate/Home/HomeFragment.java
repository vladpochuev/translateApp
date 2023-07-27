package space.lobanov.translate.Home;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import space.lobanov.translate.AdapterSetter;
import space.lobanov.translate.Languages;
import space.lobanov.translate.NetworkReceiver;
import space.lobanov.translate.R;
import space.lobanov.translate.SavedItem;
import space.lobanov.translate.Translate;

public class HomeFragment extends Fragment implements TextView.OnEditorActionListener {

    private Translate mActivity;

    private EditText source;
    private TextView result;
    private ImageButton btnTranslate, btnReset, btnCopy, btnSwap, btnSave;
    private Spinner spinnerLangFrom, spinnerLangTo;
    private RecyclerView historyList;
    private LangItemsAdapter langAdapter;
    private HistoryItemsAdapter historyAdapter;
    private AdapterSetter adapterSetter;
    private String resultText;
    private boolean isSaved;
    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        historyList.getRecycledViewPool().clear();
        historyAdapter.notifyDataSetChanged();
        historyAdapter.startListening();
        setDefinedFields();
    }

    private void setDefinedFields() {
        if(resultText != null) {
            result.setText(resultText);
        }
        if(isSaved) {
            btnSave.setImageResource(R.drawable.baseline_bookmark_24);
            btnSave.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getDefinedFields();
    }

    private void getDefinedFields() {
        resultText = result.getText().toString();
    }

    @Override
    public void onStop() {
        super.onStop();
        historyAdapter.stopListening();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setAdapters();
        setButtonsActions();
    }

    private void init(){
        plugInResources();
        setHistoryLayoutManager();
        setButtonsVisibility();
        getAdapters();
        setHistoryItemsScrolling();
        setSourceSettings();
        registerNetworkReceiver();
    }

    private void plugInResources() {
        mActivity = (Translate) getActivity();
        source = mActivity.findViewById(R.id.source);
        result = mActivity.findViewById(R.id.result);
        btnTranslate = mActivity.findViewById(R.id.btnTranslate);
        btnReset = mActivity.findViewById(R.id.btnReset);
        btnCopy = mActivity.findViewById(R.id.btnCopy);
        btnSwap = mActivity.findViewById(R.id.btnSwap);
        btnSave = mActivity.findViewById(R.id.btnSave);
        historyList = mActivity.findViewById(R.id.historyList);
        spinnerLangFrom = mActivity.findViewById(R.id.langFrom);
        spinnerLangTo = mActivity.findViewById(R.id.langTo);
        adapterSetter = new AdapterSetter(mActivity);
    }

    private void setHistoryLayoutManager() {
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        historyList.setLayoutManager(manager);
    }

    private void getAdapters() {
        langAdapter = adapterSetter.getLangAdapter();
        historyAdapter = adapterSetter.getHistoryAdapter();
    }

    private void setHistoryItemsScrolling() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int itemCount = historyAdapter.getItemCount();
                if (itemCount > 0) {
                    historyList.scrollToPosition(itemCount - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        FirebaseDatabase.getInstance().getReference().addValueEventListener(listener);
    }

    private void setSourceSettings() {
        source.setHorizontallyScrolling(false);
        source.setMaxLines(6);
        source.setOnEditorActionListener(this);
    }

    private void setAdapters(){
        setLangAdapter();
        setHistoryAdapter();
    }

    private void setLangAdapter(){
        spinnerLangFrom.setAdapter(langAdapter);
        spinnerLangTo.setAdapter(langAdapter);
        setLangSelection();
    }

    private void setLangSelection() {
        Languages localLang = adapterSetter.getLocale();
        spinnerLangFrom.setSelection(langAdapter.getPosition(Languages.English));
        spinnerLangTo.setSelection(langAdapter.getPosition(localLang));
    }

    private void setHistoryAdapter(){
        historyList.setAdapter(historyAdapter);
    }

    private void setButtonsActions() {
        btnReset.setOnClickListener(l -> onClickReset());
        btnCopy.setOnClickListener(l -> onClickCopy());
        btnSave.setOnClickListener(l -> onClickSave());
        btnSwap.setOnClickListener(l -> onClickSwap());
        btnTranslate.setOnClickListener(l -> getTranslation());
    }

    private void onClickReset() {
        source.setText("");
        result.setText("");
    }

    private void onClickCopy() {
        ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", result.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mActivity.getApplicationContext(), R.string.home_on_click_copy, Toast.LENGTH_SHORT).show();
    }

    private void onClickSave() {
        Languages langFrom = (Languages) spinnerLangFrom.getSelectedItem();
        Languages langTo = (Languages) spinnerLangTo.getSelectedItem();
        SavedItem item = new SavedItem(source.getText().toString(), result.getText().toString(),
                langTo, langFrom, FirebaseAuth.getInstance().getUid());
        item.insert();
        btnSave.setImageResource(R.drawable.baseline_bookmark_24);
        btnSave.setEnabled(false);
        isSaved = true;
    }

    private void onClickSwap() {
        Languages leftSpinner = (Languages) spinnerLangFrom.getSelectedItem();
        Languages rightSpinner = (Languages) spinnerLangTo.getSelectedItem();

        spinnerLangFrom.setSelection(langAdapter.getPosition(rightSpinner));
        spinnerLangTo.setSelection(langAdapter.getPosition(leftSpinner));
    }

    private void getTranslation(){
        if (!source.getText().toString().trim().equals("")) {
            String text = source.getText().toString().trim();
            new Translator().execute(text);
        } else {
            Toast.makeText(mActivity, R.string.home_enter_the_text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard();
            getTranslation();
        }
        return true;
    }

    public void registerNetworkReceiver() {
        mActivity.registerReceiver(new NetworkReceiver(mActivity), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }

    private void setButtonsVisibility(){
        btnReset.setVisibility(View.INVISIBLE);
        btnCopy.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                result.setText("");
                btnSave.setImageResource(R.drawable.baseline_bookmark_border_24);
                btnSave.setEnabled(true);
                if(TextUtils.isEmpty(s)){
                    btnReset.setVisibility(View.INVISIBLE);
                } else {
                    btnReset.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(TextUtils.isEmpty(s)){
                    btnCopy.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.INVISIBLE);
                } else {
                    btnCopy.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private class Translator extends AsyncTask<String, String, String> {
        HistoryItem item;
        @Override
        protected String doInBackground(String... strings) {
            item = getInfo(strings[0]);
            return executeRequest();
        }

        private HistoryItem getInfo(String text){
            Languages langFrom = (Languages) spinnerLangFrom.getSelectedItem();
            Languages langTo = (Languages) spinnerLangTo.getSelectedItem();

            return new HistoryItem(text, langTo, langFrom, FirebaseAuth.getInstance().getUid(), getUNIX());
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
            Request request = new Request.Builder()
                    .url("https://api-b2b.backenster.com/b1/api/v3/translate")
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", "a_fhvZYBPKemWAf00YLModU78yhoTduTE6gDzuibkunlUmch4GO9lOZp0DImFijwpdqKpGK8r3s8laNkoM")
                    .build();

            return request;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            String translation;
            try {
                translation = parseJSON(json);
            } catch (RuntimeException e) {
                return;
            }
            result.setText(translation);
            hideKeyboard();

            item.setResult(translation);
            item.insert();
        }

        private String parseJSON(String s){
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(!jsonObject.getString("err").equals("null") ) {
                    throw new RuntimeException();
                } else {
                    return jsonObject.getString("result");
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
