package Vlad_kv;

import org.bson.Document;

public class User {
    public final int id;
    public final String currency;


    public User(Document doc) {
        this(doc.getInteger("id"), doc.getString("currency"));
    }

    public User(int id, String currency) {
        this.id = id;
        this.currency = currency;
    }

    @Override
    public String toString() {
        Document document = new Document();
        document.append("id", id)
                .append("currency", currency);
        return document.toJson();
    }
}