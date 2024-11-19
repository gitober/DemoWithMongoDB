package org.example.demowithmongodb;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField cityField;

    private MongoCollection<Document> collection;

    // Setter method to inject the MongoDatabase instance
    public void setMongoDatabase(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("MongoDatabase cannot be null");
        }
        this.collection = database.getCollection("documents");
        logger.info("MongoCollection initialized successfully.");
    }

    // Add a document to the collection
    @FXML
    private void addDocument() {
        try {
            Document doc = new Document("_id", idField.getText())
                    .append("name", nameField.getText())
                    .append("age", Integer.parseInt(ageField.getText()))
                    .append("city", cityField.getText());
            collection.insertOne(doc);
            showAlert("Success", "Document added successfully!");
            logger.info("Document added: {}", doc.toJson());
            clearTextFields(); // Clear text fields after operation
        } catch (Exception e) {
            showAlert("Error", "Failed to add document: " + e.getMessage());
            logger.error("Failed to add document", e);
        }
    }

    // Read a document from the collection
    @FXML
    private void readDocument() {
        try {
            Document doc = collection.find(Filters.eq("_id", idField.getText())).first();
            if (doc != null) {
                // Display document details in the alert dialog
                StringBuilder details = new StringBuilder();
                details.append("ID: ").append(doc.getString("_id")).append("\n")
                        .append("Name: ").append(doc.getString("name")).append("\n")
                        .append("Age: ").append(doc.getInteger("age")).append("\n")
                        .append("City: ").append(doc.getString("city"));

                showAlert("Success", "Document retrieved successfully!\n\n" + details.toString());
                logger.info("Document retrieved: {}", doc.toJson());
            } else {
                // If no document is found, show an error message
                showAlert("Error", "No document found with the given ID.");
                logger.warn("No document found with ID: {}", idField.getText());
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during the read operation
            showAlert("Error", "Failed to retrieve document: " + e.getMessage());
            logger.error("Failed to retrieve document", e);
        } finally {
            // Clear the text fields
            clearTextFields();
        }
    }

    // Update a document in the collection
    @FXML
    private void updateDocument() {
        try {
            // Retrieve the existing document by ID
            Document existingDoc = collection.find(Filters.eq("_id", idField.getText())).first();

            if (existingDoc != null) {
                // Prepare the updated fields, keeping unchanged fields intact
                Document updatedDoc = new Document();
                updatedDoc.append("name", nameField.getText().isEmpty() ? existingDoc.getString("name") : nameField.getText());
                updatedDoc.append("age", ageField.getText().isEmpty() ? existingDoc.getInteger("age") : Integer.parseInt(ageField.getText()));
                updatedDoc.append("city", cityField.getText().isEmpty() ? existingDoc.getString("city") : cityField.getText());

                // Build a string to show what was updated
                StringBuilder changes = new StringBuilder("Updated fields:\n");
                if (!nameField.getText().isEmpty() && !nameField.getText().equals(existingDoc.getString("name"))) {
                    changes.append("Name: ").append(existingDoc.getString("name")).append(" → ").append(nameField.getText()).append("\n");
                }
                if (!ageField.getText().isEmpty() && !ageField.getText().equals(String.valueOf(existingDoc.getInteger("age")))) {
                    changes.append("Age: ").append(existingDoc.getInteger("age")).append(" → ").append(ageField.getText()).append("\n");
                }
                if (!cityField.getText().isEmpty() && !cityField.getText().equals(existingDoc.getString("city"))) {
                    changes.append("City: ").append(existingDoc.getString("city")).append(" → ").append(cityField.getText()).append("\n");
                }

                // Update the document in the database
                collection.updateOne(Filters.eq("_id", idField.getText()), new Document("$set", updatedDoc));
                showAlert("Success", "Document updated successfully!\n\n" + changes.toString());
                logger.info("Document updated with ID: {} - New Data: {}", idField.getText(), updatedDoc.toJson());
            } else {
                showAlert("Error", "No document found with the given ID.");
                logger.warn("No document found with ID: {}", idField.getText());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to update document: " + e.getMessage());
            logger.error("Failed to update document", e);
        } finally {
            clearTextFields(); // Clear text fields after operation
        }
    }

    // Delete a document from the collection
    @FXML
    private void deleteDocument() {
        try {
            collection.deleteOne(Filters.eq("_id", idField.getText()));
            showAlert("Success", "Document deleted successfully!");
            logger.info("Document deleted with ID: {}", idField.getText());
            clearTextFields(); // Clear text fields after operation
        } catch (Exception e) {
            showAlert("Error", "Failed to delete document: " + e.getMessage());
            logger.error("Failed to delete document", e);
        }
    }

    // Helper method to clear all text fields
    private void clearTextFields() {
        idField.clear();
        nameField.clear();
        ageField.clear();
        cityField.clear();
    }

    // Helper method to show an alert dialog
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
