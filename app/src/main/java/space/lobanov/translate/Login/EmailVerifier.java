package space.lobanov.translate.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import space.lobanov.translate.R;
import space.lobanov.translate.Translate;

public class EmailVerifier extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private TextView tvMessage;
    private Handler mHandler;

    public static final String isVerificationCanceled = "CANCELED";
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            testIfEmailVerified();
            mHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verifier);

        init();
        setTextViewMessage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendEmailVer();
        mHandler.postDelayed(mRunnable, 5000);
    }

    private void testIfEmailVerified() {
        cUser.reload().addOnCompleteListener(task -> {
            if (cUser.isEmailVerified()) {
                Toast.makeText(this, "Account has been successfully created", Toast.LENGTH_SHORT).show();
                mHandler.removeCallbacks(mRunnable);
                getIntoActivity();
            }
        });
    }
    private void getIntoActivity(){
        Intent intent = new Intent(EmailVerifier.this, Translate.class);
        startActivity(intent);
        finish();
    }
    private void sendEmailVer() {
        cUser.sendEmailVerification().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Check your email to verify the address", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Email failed to send", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init() {
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();
        tvMessage = findViewById(R.id.tvMessage);
        mHandler = new Handler();
    }
    private void setTextViewMessage() {
        String message = "To complete account setup, you need to verify %s. \n Please check your " +
                "email.";
        String email = cUser.getEmail();
        tvMessage.setText(String.format(message, email));
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(EmailVerifier.this, MainActivity.class);
        intent.putExtra(isVerificationCanceled, true);
        mHandler.removeCallbacks(mRunnable);
        startActivity(intent);
        finish();
    }
}
