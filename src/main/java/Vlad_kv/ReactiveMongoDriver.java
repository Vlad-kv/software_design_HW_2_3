package Vlad_kv;

import com.mongodb.rx.client.*;
import org.bson.Document;
import rx.Observable;
import rx.functions.Func0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class ReactiveMongoDriver {
    private static MongoClient client = createMongoClient();

    public static final String databaseName = "My_database";

    private static MongoClient createMongoClient() {
        return MongoClients.create();
    }

    public static Observable<Success> addUser(int id, String currency) {
        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("user");
        Document document = Document.parse(new User(id, currency).toString());
        return collection.insertOne(document);
    }

    public static Observable<Success> addProduct(String name, Double cost) {
        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("product");
        Document document = Document.parse(new Product(name, cost).toString());
        return collection.insertOne(document);
    }

    public static Observable<User> getUser(int id) {
        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("user");
        return collection.aggregate(new ArrayList<>()).toObservable()
                .map(User::new)
                .filter(u -> u.id == id)
                .firstOrDefault(new User(0, "USD"));
    }

    public static Observable<TreeMap<String, String>> getProducts(Double modifier) {
        MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection("product");
        AggregateObservable<Document> res = collection.aggregate(new ArrayList<>());

        return res.toObservable().collect((Func0<TreeMap<String, String>>) TreeMap::new, (m, v) -> {
            Product product = new Product(v);
            m.put(product.name, String.valueOf(product.cost * modifier));
        });
    }

    public static Observable<TreeMap<String, String>> getUsers() {
        MongoCollection<Document> collection = client.getDatabase(databaseName).getCollection("user");
        AggregateObservable<Document> res = collection.aggregate(new ArrayList<>());

        return res.toObservable().collect((Func0<TreeMap<String, String>>) TreeMap::new, (m, v) -> {
            User user = new User(v);
            m.put(String.valueOf(user.id), user.currency);
        });
    }
}
