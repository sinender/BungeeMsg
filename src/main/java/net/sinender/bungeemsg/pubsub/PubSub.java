package net.sinender.bungeemsg.pubsub;

import net.sinender.bungeemsg.BungeeMsg;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;

import java.util.*;

public class PubSub {
    HashMap<String, Callback> callbacks = new HashMap<>();
    ArrayList<String> listened = new ArrayList<>();
    private final BungeeMsg plugin;
    private final MongoDBPS pubsubDB;

    public PubSub(BungeeMsg plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Starting PubSub");
        MongoDBPS.connect();
        pubsubDB = new MongoDBPS(BungeeMsg.config.getString("mongo.pubsub"));
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 200);
    }

    public void publish(String key, Object... objects) {
        String s = "";
        for (Object obj : objects) {
            s += ";" + obj.toString();
        }
        publish(key, s.substring(1));
    }

    public void publish(String key, String msg) {
        Document doc = new Document("key", key).append("msg", msg).append("uuid", UUID.randomUUID().toString());
        pubsubDB.setDocument(key, doc);
    }

    public void registerListener(String channel, Callback callback) {
        callbacks.putIfAbsent(channel, callback);
        System.out.println("Registering a new pubsub listener for " + channel);
    }
    public void update() {
        for (Document doc : pubsubDB.getDocuments()) {
            String key = doc.getString("key");
            String msg = doc.getString("msg");
            String uuid = doc.getString("uuid");
            if (CollectionUtils.containsAny(listened, Collections.singletonList(uuid))) {
                continue;
            }
            if (CollectionUtils.containsAny(callbacks.keySet(), Collections.singletonList(key))) {
                Callback callback = callbacks.get(key);
                listened.add(uuid);
                callback.onMessage(msg.split(";"));
            }
        }
    }
}
