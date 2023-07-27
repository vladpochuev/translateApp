package space.lobanov.translate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import space.lobanov.translate.Home.HomeFragment;
import space.lobanov.translate.List.ListFragment;
import space.lobanov.translate.Quiz.QuizFragment;

public class Translate extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ListFragment listFragment = new ListFragment();
    QuizFragment quizFragment = new QuizFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
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