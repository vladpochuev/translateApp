package space.lobanov.translate.Login;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;

import space.lobanov.translate.NetworkReceiver;
import space.lobanov.translate.R;
import space.lobanov.translate.Translate;

public class MainActivity extends AppCompatActivity {
    private EditText edEmail, edPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;

    private NetworkReceiver networkReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNextActions();
    }
    private void getNextActions() {
        cUser = mAuth.getCurrentUser();
        if(cUser != null) {
            if (cUser.isEmailVerified()) {
                getIntoActivity(Translate.class);
            } else if(!getIntent().hasExtra(EmailVerifier.isVerificationCanceled)) {
                getIntoActivity(EmailVerifier.class);
            }
        }
    }

    public void onClickSignIn(View view) {
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            if (areFieldsFilled(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        cUser = mAuth.getCurrentUser();
                        getNextActions();
                    } else {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        ErrorHandler handler = new ErrorHandler(this, errorCode);
                        handler.showErrorMessage();
                    }
                });
            }
    }
    public void onClickSignUp(View view) {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        if(areFieldsFilled(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    cUser = mAuth.getCurrentUser();
                    getNextActions();
                } else {
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    ErrorHandler handler = new ErrorHandler(this, errorCode);
                    handler.showErrorMessage();
                }
            });
        }
    }

    private boolean areFieldsFilled(String email, String password) {
        return !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password);
    }

    private void init() {
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        mAuth = FirebaseAuth.getInstance();
        networkReceiver = new NetworkReceiver(this);
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void getIntoActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        unregisterReceiver(networkReceiver);
        startActivity(intent);
        finish();
    }
}
