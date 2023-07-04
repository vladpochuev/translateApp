package space.lobanov.translate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import space.lobanov.translate.Fragments.HomeFragment;
import space.lobanov.translate.Fragments.ListFragment;
import space.lobanov.translate.Fragments.QuizFragment;

public class Translate extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = HomeFragment.newInstance();
    ListFragment listFragment = ListFragment.newInstance();

    QuizFragment quizFragment = QuizFragment.newInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                case R.id.list:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
                    return true;
                case R.id.quiz:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, quizFragment).commit();
                    return true;
            }
            return false;
        });
    }
}
