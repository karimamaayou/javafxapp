package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AddReservationController {

    @FXML
    private TextField PriceField;

    @FXML
    private Button ReserverButtonId;

    @FXML
    private Label clientErrorLabel;

    @FXML
    private ComboBox<String> clientField;

    @FXML
    private Label dateDebutErrorLabel;

    @FXML
    private DatePicker dateDebutField;

    @FXML
    private Label dateFinErrorLabel;

    @FXML
    private DatePicker dateFinField;

    @FXML
    private Label prestataireErrorLabel;

    @FXML
    private ComboBox<String> prestataireField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private Button viewReservationButtonID;

   

    private Map<String, Integer> clientMap = new HashMap<>();
    private Map<String, Integer> prestataireMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Charger les clients et prestataires depuis la base de données
        loadClientsFromDatabase();
        loadPrestatairesFromDatabase();

        // Ajouter des écouteurs pour effacer les messages d'erreur
        clientField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clientErrorLabel.setText("");
            }
        });

        prestataireField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                prestataireErrorLabel.setText("");
                // Charger le prix correspondant au prestataire sélectionné
                loadPrixForPrestataire(newValue);
            }
        });

        dateDebutField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateDebutErrorLabel.setText("");
            }
        });

        dateFinField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateFinErrorLabel.setText("");
            }
        });
    }

    @FXML
    void ReserverButton(ActionEvent event) {
        boolean hasError = false;

        // Réinitialiser les messages d'erreur
        clientErrorLabel.setText("");
        prestataireErrorLabel.setText("");
        dateDebutErrorLabel.setText("");
        dateFinErrorLabel.setText("");
        priceErrorLabel.setText("");

        // Validation des champs
        if (clientField.getSelectionModel().isEmpty()) {
            clientErrorLabel.setText("Veuillez sélectionner un client.");
            hasError = true;
        }

        if (prestataireField.getSelectionModel().isEmpty()) {
            prestataireErrorLabel.setText("Veuillez sélectionner un prestataire.");
            hasError = true;
        }

        if (dateDebutField.getValue() == null) {
            dateDebutErrorLabel.setText("Veuillez entrer la date de début.");
            hasError = true;
        }

        if (dateFinField.getValue() == null) {
            dateFinErrorLabel.setText("Veuillez entrer la date de fin.");
            hasError = true;
        }

        if (dateDebutField.getValue() != null && dateFinField.getValue() != null) {
            if (dateDebutField.getValue().isAfter(dateFinField.getValue())) {
                dateDebutErrorLabel.setText("La date de début doit être antérieure à la date de fin.");
                hasError = true;
            }
        }

        String prixStr = PriceField.getText();
        if (prixStr == null || prixStr.isEmpty()) {
            priceErrorLabel.setText("Veuillez entrer le prix.");
            hasError = true;
        }

        double prix = 0;
        try {
            prix = Double.parseDouble(prixStr);
            if (prix <= 0) {
                priceErrorLabel.setText("Le prix doit être supérieur à zéro.");
                hasError = true;
            }
        } catch (NumberFormatException e) {
            priceErrorLabel.setText("Le prix doit être un nombre valide.");
            hasError = true;
        }

        if (!hasError) {
            try (Connection connection = MysqlConnection.getDBConnection()) {
                // Récupérer les IDs sélectionnés
                int clientId = clientMap.get(clientField.getSelectionModel().getSelectedItem());
                int prestataireId = prestataireMap.get(prestataireField.getSelectionModel().getSelectedItem());
                int statut_id = 1;

                // Insérer la réservation avec le prix
                String sql = "INSERT INTO reservation (client_id, prestataire_id, date_debut, date_fin, prix,statut_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, clientId);
                    ps.setInt(2, prestataireId);
                    ps.setObject(3, dateDebutField.getValue());
                    ps.setObject(4, dateFinField.getValue());
                    ps.setDouble(5, prix);
                    ps.setInt(6, statut_id);
                    ps.executeUpdate();
                }

               
                DBUtils.changeScene( event, "reservationTable.fxml");

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            }
        }
    }
    
    


    private void loadPrixForPrestataire(String prestataireName) {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String query = "SELECT prix FROM reservation WHERE reservation_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, prestataireName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        double prix = rs.getDouble("prix");
                        PriceField.setText(String.valueOf(prix));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du prix : " + e.getMessage());
        }
    }

    private void loadClientsFromDatabase() {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String query = "SELECT client_id, CONCAT(nom, ' ', prenom) AS fullName FROM client";
            try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

                clientField.getItems().clear();
                while (rs.next()) {
                    int clientId = rs.getInt("client_id");
                    String fullName = rs.getString("fullName");
                    clientField.getItems().add(fullName);
                    clientMap.put(fullName, clientId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void loadPrestatairesFromDatabase() {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String query = "SELECT prestataire_id, CONCAT(nom, ' ', prenom) AS fullName FROM prestataire";
            try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

                prestataireField.getItems().clear();
                while (rs.next()) {
                    int prestataireId = rs.getInt("prestataire_id");
                    String fullName = rs.getString("fullName");
                    prestataireField.getItems().add(fullName);
                    prestataireMap.put(fullName, prestataireId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des prestataires : " + e.getMessage());
        }
    }

   
    	
    
}
