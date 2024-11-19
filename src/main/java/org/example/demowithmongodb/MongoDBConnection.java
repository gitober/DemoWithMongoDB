package org.example.demowithmongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private MongoClient mongoClient;

    public MongoDatabase connect() {
        // Load environment variables (.env file)
        Dotenv dotenv = Dotenv.load();
        String connectionString = dotenv.get("MONGO_URI");
        String databaseName = dotenv.get("MONGO_DATABASE");

        if (connectionString == null || databaseName == null) {
            throw new RuntimeException("MONGO_URI or MONGO_DATABASE is not set in the .env file.");
        }

        // Configure MongoDB settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();

        // Create the MongoClient and keep it open
        mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        try {
            // Ping the database to ensure the connection is successful
            database.runCommand(new Document("ping", 1));
            logger.info("Successfully connected to MongoDB.");
        } catch (Exception e) {
            logger.error("Failed to ping MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("MongoDB connection failed", e);
        }

        return database;
    }

    // Close the MongoClient connection
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("MongoClient connection closed.");
        }
    }
}
