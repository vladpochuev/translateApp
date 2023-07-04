package space.lobanov.translate.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import space.lobanov.translate.AnswersHolder;
import space.lobanov.translate.LanguageCollector;
import space.lobanov.translate.Languages;
import space.lobanov.translate.R;
import space.lobanov.translate.SavedItem;
import space.lobanov.translate.Translate;

public class QuizFragment extends Fragment implements View.OnClickListener {
    private Translate mActivity;
    private TextView tvTarget, tvLanguage;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4, btnConfirm, btnCorrect, btnWrong;

    private Languages targetLanguage;
    private ArrayList<SavedItem> allItems;
    private ArrayList<SavedItem> answers;
    private ArrayList<Button> buttons;
    private String correctAnswer;
    private String selectedAnswer;

    private QuizFragment(){}

    public static QuizFragment newInstance(){
        return new QuizFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setUpButtons();
        loadQuestion();
    }

    private void init() {
        mActivity = (Translate) getActivity();
        tvTarget = mActivity.findViewById(R.id.tvTarget);
        tvLanguage = mActivity.findViewById(R.id.tvLanguage);
        btnAnswer1 = mActivity.findViewById(R.id.btnAnswer1);
        btnAnswer2 = mActivity.findViewById(R.id.btnAnswer2);
        btnAnswer3 = mActivity.findViewById(R.id.btnAnswer3);
        btnAnswer4 = mActivity.findViewById(R.id.btnAnswer4);
        btnConfirm = mActivity.findViewById(R.id.btnConfirm);
        btnCorrect = mActivity.findViewById(R.id.btnCorrect);
        btnWrong = mActivity.findViewById(R.id.btnWrong);
        allItems = new ArrayList<>();
    }

    private void setUpButtons() {
        buttons = new ArrayList<Button>(){{
            add(btnAnswer1);
            add(btnAnswer2);
            add(btnAnswer3);
            add(btnAnswer4);
        }};
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
        btnConfirm.setOnClickListener(l -> onClickConfirm());
        btnCorrect.setOnClickListener(l -> getNextQuestion());
        btnWrong.setOnClickListener(l -> getNextQuestion());
    }

    private void setButtonsVisibility() {
        btnCorrect.setVisibility(View.INVISIBLE);
        btnWrong.setVisibility(View.INVISIBLE);
    }
    private void loadQuestion() {
        SavedItem.getElements(items -> {
            allItems = items;
            getTargetLanguage();
            isLanguageEnabled();
            getAnswers();
            setData();
            setButtonsVisibility();
        });
    }

    private void getTargetLanguage() {
        LanguageCollector collector = new LanguageCollector(allItems);
        targetLanguage = collector.pickLanguage();
    }

    private void isLanguageEnabled() {
        if(targetLanguage == null) {

        }
    }

    private void getAnswers() {
        AnswersHolder answersHolder = new AnswersHolder(allItems, targetLanguage);
        answers = answersHolder.getRandomAnswers();
    }

    private void setData() {
        for (int i = 0; i < buttons.size(); i++) {
            String question = answers.get(i).getResult();
            buttons.get(i).setText(question);
        }
        tvLanguage.setText(targetLanguage.name());
        determineCorrectAnswer();
    }
    private void determineCorrectAnswer() {
        int randomIndex = (int) Math.round(Math.random() * (answers.size() - 1));
        SavedItem answer = answers.get(randomIndex);
        correctAnswer = answer.getResult();
        tvTarget.setText(answer.getSource());
    }

    @Override
    public void onClick(View view) {
        restoreButtons();
        Button button = (Button) view;
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.confirm_button_theme);
        selectedAnswer = button.getText().toString().trim();
    }

    private void restoreButtons() {
        for (Button button : buttons) {
            button.setBackgroundResource(R.drawable.answer_button_selector);
            button.setEnabled(true);
        }
    }

    private void onClickConfirm() {
        if(selectedAnswer == null) {
            Toast.makeText(mActivity, "Ответ не выбран", Toast.LENGTH_SHORT).show();
        } else {
            determineOutcome();
        }
    }
    private void determineOutcome() {
        setUIForAnswers();
        if(correctAnswer.equals(selectedAnswer)) {
            btnCorrect.setVisibility(View.VISIBLE);
        } else {
            btnWrong.setVisibility(View.VISIBLE);
        }
    }

    private void setUIForAnswers() {
        for (Button button : buttons) {
            button.setEnabled(false);
            String answer = button.getText().toString().trim();
            if(answer.equals(selectedAnswer)) {
                button.setBackgroundResource(R.drawable.wrong_button_theme);
            }
            if(answer.equals(correctAnswer)) {
                button.setBackgroundResource(R.drawable.correct_button_theme);
            }
        }
    }

    private void getNextQuestion() {
        restoreButtons();
        loadQuestion();
    }
}
