package space.lobanov.translate;

import java.util.ArrayList;

public class AnswersHolder {
    public static final int NUMBER_OF_ANSWERS = 4;
    private ArrayList<SavedItem> items;
    private Languages language;

    public AnswersHolder(ArrayList<SavedItem> allItems, Languages language) {
        this.items = allItems;
        this.language = language;
    }

    public ArrayList<SavedItem> getRandomAnswers() {
        ArrayList<SavedItem> answers = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
            int randomIndex = (int) Math.round(Math.random() * (items.size()-1));
            SavedItem item = items.get(randomIndex);
            if(answers.contains(item) || item.getLangTo() != language) {
                i--;
                continue;
            }
            answers.add(item);
        }
        return answers;
    }
}
