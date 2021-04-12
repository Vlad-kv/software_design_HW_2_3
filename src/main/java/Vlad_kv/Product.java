package Vlad_kv;

import org.bson.Document;

public class Product {
    public final String name;
    public final Double cost;


    public Product(Document doc) {
        this(doc.getString("name"), doc.getDouble("cost"));
    }

    public Product(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    @Override
    public String toString() {
        Document document = new Document();
        document.append("name", name)
                .append("cost", cost);
        return document.toJson();
    }
}
