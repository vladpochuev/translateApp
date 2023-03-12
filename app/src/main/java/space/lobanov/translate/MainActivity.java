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

public class MainActivity extends AppCompatActivity {
    private static EditText login;
    private static EditText password;
    private static Button button;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private static Switch switcher;
    DBHelper dbHelper;

    String users_login;
    String users_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //скрываем нижнюю панель навигации
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY); // появляется поверх приложения и исчезает

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);
        switcher = findViewById(R.id.switcher);

        dbHelper = DBHelper.create(this);


        button.setOnClickListener(l -> {
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
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, String.format("%s = '%s'", DBHelper.KEY_LOGIN, users_login), null, null, null, null);

        if(!cursor.moveToFirst()){
            Toast.makeText(this,"Неверный логин",Toast.LENGTH_LONG).show();
        } else {
            int passwordIndex = cursor.getColumnIndex(DBHelper.KEY_PASSWORD);
            String password = cursor.getString(passwordIndex);

            if(users_password.equals(password)){
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int id = cursor.getInt(idIndex);
                recordInfo(id, users_login);
            } else {
                Toast.makeText(this,"Неверный пароль",Toast.LENGTH_LONG).show();
            }
        }
        cursor.close();
        database.close();
    }

    private void signUp() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, String.format("%s = '%s'", DBHelper.KEY_LOGIN, users_login), null, null, null, null);
        if (cursor.getCount()!=0) {
            Toast.makeText(this,"Пользователь уже зарегистрирован", Toast.LENGTH_LONG).show();
        } else {
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.KEY_LOGIN, users_login);
            contentValues.put(DBHelper.KEY_PASSWORD, users_password);
            database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

            cursor = database.query(DBHelper.TABLE_CONTACTS, null, String.format("%s = '%s'", DBHelper.KEY_LOGIN, users_login), null, null, null, null);
            cursor.moveToFirst();
            int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int id = cursor.getInt(indexId);

            recordInfo(id, users_login);
            cursor.close();
        }
        cursor.close();
        dbHelper.close();
    }

    private void recordInfo(int id, String login){
        new User(id, login);
        System.out.println(User.user);
        Intent intent = new Intent(MainActivity.this, Translate.class);
        startActivity(intent);
        finish();
    }
}
