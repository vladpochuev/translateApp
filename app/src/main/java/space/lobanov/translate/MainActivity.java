package space.lobanov.translate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    EditText edEmail, edPassword;
    FirebaseAuth mAuth;
    FirebaseUser cUser;
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
            } else {
                getIntoActivity(EmailVerifier.class);
            }
        }
    }

    public void onClickSignIn(View view) {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        if(!areFieldsEmpty(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
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
    public void onClickSignUp(View view) {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        if(!areFieldsEmpty(email, password)) {
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

    private boolean areFieldsEmpty(String email, String password) {
        return TextUtils.isEmpty(email) || TextUtils.isEmpty(password);
    }

    private void init() {
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    private void getIntoActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
        finish();
    }
}
