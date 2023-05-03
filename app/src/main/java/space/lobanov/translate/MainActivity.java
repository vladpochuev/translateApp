package space.lobanov.translate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import space.lobanov.translate.Database.DBHelper;
import space.lobanov.translate.Database.UsersTable;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static EditText login;
    @SuppressLint("StaticFieldLeak")
    private static EditText password;
    @SuppressLint("StaticFieldLeak")
    private static Button btnContinue;
    @SuppressLint({"UseSwitchCompatOrMaterialCode", "StaticFieldLeak"})
    private static Switch switcher;
    private String users_login;
    private String users_password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// - скрыть нижнюю панель навигации
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);//- нижняя панель будет появляться только при вызове и исчезать через несколько секунд


        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnContinue = findViewById(R.id.btnContinue);
        switcher = findViewById(R.id.switcher);

        DBHelper.database = DBHelper.create(this);


        btnContinue.setOnClickListener(l -> {
            users_login = login.getText().toString().trim();
            users_password = password.getText().toString().trim();

            if(users_login.equals("") || users_password.equals("")){
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_LONG).show();
            }

            if(switcher.isChecked()){
                signUp();
            } else {
                signIn();
            }
        });
    }
    private void signIn(){
        SQLiteDatabase database = DBHelper.database.getReadableDatabase();
        UsersTable table = new UsersTable();
        Cursor cursor = database.query(table.CONTACTS, null, String.format("%s = '%s'", table.LOGIN, users_login), null, null, null, null);

        if(!cursor.moveToFirst()){
            Toast.makeText(this,"Неверный логин",Toast.LENGTH_LONG).show();
        } else {
            int passwordIndex = cursor.getColumnIndex(table.PASSWORD);
            String password = cursor.getString(passwordIndex);

            if(users_password.equals(password)){
                int idIndex = cursor.getColumnIndex(table.ID);
                long id = cursor.getInt(idIndex);
                recordInfo(id, users_login);
            } else {
                Toast.makeText(this,"Неверный пароль",Toast.LENGTH_LONG).show();
            }
        }
        cursor.close();
        database.close();
    }

    private void signUp() {
        SQLiteDatabase database = DBHelper.database.getWritableDatabase();
        UsersTable table = new UsersTable();
        Cursor cursor = database.query(table.CONTACTS, null, String.format("%s = '%s'", table.LOGIN, users_login), null, null, null, null);
        if (cursor.getCount()!=0) {
            Toast.makeText(this,"Пользователь уже зарегистрирован", Toast.LENGTH_LONG).show();
        } else {
            ContentValues contentValues = new ContentValues();

            contentValues.put(table.LOGIN, users_login);
            contentValues.put(table.PASSWORD, users_password);
            long id = database.insert(table.CONTACTS, null, contentValues);

            recordInfo(id, users_login);
            cursor.close();
            database.close();
        }
        cursor.close();
        DBHelper.database.close();
    }

    private void recordInfo(long id, String login){
        new User(id, login);
        System.out.println(User.user);
        Intent intent = new Intent(MainActivity.this, Translate.class);
        startActivity(intent);
        finish();
    }
}
