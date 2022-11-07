package net.sinender.bungeemsg.pubsub;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sinender.bungeemsg.BungeeMsg;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBPS {
    private final MongoCollection<Document> collection;
    private static MongoClient client;
    private static MongoDatabase database;

    public MongoDBPS(String collection) {
        this.collection = database.getCollection(collection);
    }

    public static void connect() {
        // connect to mongoDB
        BungeeMsg.getInstance().getLogger().info("Connecting to MongoDB...");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(BungeeMsg.config.getString("mongo.uri")))
                .applyToSslSettings(x -> x.enabled(BungeeMsg.config.getBoolean("mongo.useSSL")))
                .build();
        client = MongoClients.create(settings);
        database = client.getDatabase(BungeeMsg.config.getString("mongo.database"));
        BungeeMsg.getInstance().getLogger().info("Successfully connected to MongoDB!");
    }

    public Document getDocument(String uuid) {
        // get document from mongoDB
        Document query = new Document("key", uuid);
        return collection.find(query).first();
    }

    public List<Document> getDocuments() {
        return collection.find().into(new ArrayList<>());
    }

    public Document getLatestDocument() {
        return collection.find().sort(new Document("_id", -1)).first();
    }

    public void setDocument(String uuid, Document document) {
        // set document in mongoDB
        collection.insertOne(document);
    }

    public Document updateDocument(Document document) {
        Document query = new Document("key", document.getString("key"));
        if (collection.find(query).first() == null) {
            collection.insertOne(document);
        } else {
            collection.replaceOne(query, document);
        }
        return document;
    }
}
