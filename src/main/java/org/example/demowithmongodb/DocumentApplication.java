package org.example.demowithmongodb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(DocumentApplication.class);

    @Override
    public void start(Stage stage) {
        MongoDatabase mongoDatabase;
        // Use MongoDBConnection to connect to the database
        MongoDBConnection connection = new MongoDBConnection();
        mongoDatabase = connection.connect();
        logger.info("Connected to MongoDB successfully!");

        // Load JavaFX FXML file and pass MongoDB instance to the controller
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DocumentApplication.class.getResource("document-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 300);
            DocumentController controller = fxmlLoader.getController();
            controller.setMongoDatabase(mongoDatabase); // Pass the database to the controller
            stage.setTitle("MongoDB CRUD Operations");
            stage.setScene(scene);
            stage.show();
            logger.info("JavaFX application started successfully.");
        } catch (Exception e) {
            logger.error("Failed to load the JavaFX application: {}", e.getMessage(), e);
            System.exit(1); // Exit the application if loading fails
        }
    }

    // Launch the JavaFX application
    public static void main(String[] args) {
        launch();
    }
}
