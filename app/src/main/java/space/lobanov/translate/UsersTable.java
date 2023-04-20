package space.lobanov.translate;

public class UsersTable {
    public final String CONTACTS = "users";

    public final String ID = "_id";
    public final String LOGIN = "_login";
    public final String PASSWORD = "_password";

    public String getCreateQuery(){
        return String.format(Queries.CREATE.QUERY, CONTACTS,
                ID, LOGIN, PASSWORD);
    }

    public String getUpdateQuery(){
        return String.format(Queries.UPGRADE.QUERY, CONTACTS);
    }

    enum Queries {
        CREATE("CREATE TABLE IF NOT EXISTS " +
                "%s( %s INTEGER PRIMARY KEY AUTOINCREMENT," + // TABLE_CONTACTS, KEY_ID
                " %s VARCHAR(32)," + // KEY_LOGIN
                " %s VARCHAR(32) );"), // KEY_PASSWORD
        UPGRADE("DROP TABLE IF EXISTS %s"); // TABLE_CONTACTS
        final String QUERY;
        Queries(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
