package space.lobanov.translate;

import androidx.annotation.NonNull;

public class User {
    public static User user;
    private long id;
    private String login;

    public User(long id, String login) {
        this.id = id;
        this.login = login;

        user = this;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
