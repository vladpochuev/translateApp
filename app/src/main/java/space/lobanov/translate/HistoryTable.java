package space.lobanov.translate;

public class HistoryTable {
    public final String CONTACTS = "history";

    public final String USER_ID = "_user_id";
    public final String SOURCE_LANG = "_source_lang";
    public final String RESULT_LANG = "_result_lang";
    public final String SOURCE = "_source";
    public final String RESULT = "_result";
    public final String DATE = "_date";

    public String getCreateQuery(){
        return String.format(Queries.CREATE.QUERY, CONTACTS,
                USER_ID, SOURCE_LANG, RESULT_LANG, SOURCE, RESULT, DATE);
    }

    public String getUpdateQuery(){
        return String.format(Queries.UPDATE.QUERY, CONTACTS);
    }

    enum Queries {
        CREATE("CREATE TABLE IF NOT EXISTS " +
                "%s(%s INTEGER, " + // TABLE_CONTACTS, USER_ID
                "%s VARCHAR(32), " +  //  SOURCE_LANG
                "%s VARCHAR(32), " + // RESULT_LANG
                "%s VARCHAR(256), " + // SOURCE
                "%s VARCHAR(256), " + // RESULT
                "%s VARCHAR(40) );"), // DATE
        UPDATE("DROP TABLE IF EXISTS %s"); // TABLE_CONTACTS
        final String QUERY;
        Queries(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
