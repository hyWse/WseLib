/*
 * Copyright (c) 2018.
 * Created at 18.10.2018
 * ---------------------------------------------
 * @author hyWse
 * @see https://hywse.eu
 * ---------------------------------------------
 * If you have any questions, please contact
 * E-Mail: admin@hywse.eu
 * Discord: hyWse#0126
 */

package eu.hywse.lib.databases.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.hywse.lib.text.WseStringUtil;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;

public class MongoManager {

    private MongoClient client;
    private MongoDatabase database;

    private HashMap<String, MongoCollection<Document>> collections;

    @Getter
    private boolean connected;
    @Getter
    private String errorMessage;

    public MongoManager(String str) {
        this.collections = new HashMap<>();

        // Connection
        ConnectionString conn = new ConnectionString(str);
        String database = !conn.getDatabase().equalsIgnoreCase("null") ? conn.getDatabase() : "admin";
        System.out.println("Connecting to " + String.join(", ", conn.getHosts()) + " / " + database);

        boolean found = false;

        try {

            // Build mongo client
            this.client = MongoClients.create(conn);

            for (String name : client.listDatabaseNames()) {
                if (name.equalsIgnoreCase(database)) {
                    found = true;
                    break;
                }
            }

        } catch (Exception ex) {
            // ex.printStackTrace();
            connected = false;
            errorMessage = ex.getMessage();
            return;
        }

        connected = found;

        if (!found) {
            String msg = "Database " + database + " not found!";
            String sep = WseStringUtil.repeat("#", msg.length());

            System.out.println(sep);
            System.out.println(msg);
            System.out.println(sep);

            errorMessage = "Database not found";
            return;
        }

        this.database = client.getDatabase(database);
    }

    public MongoCollection<Document> get(String name) {
        if (!collections.containsKey(name)) {
            collections.put(name, database.getCollection(name));
        }
        return collections.get(name);
    }

}
