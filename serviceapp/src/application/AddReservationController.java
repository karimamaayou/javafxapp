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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AddReservationController {

    @FXML
    private Button ReserverButtonId;

    @FXML
    private ComboBox<String> prestataireField;

    @FXML
    private ComboBox<String> clientField;

    @FXML
    private TextField dateDebutField;

    @FXML
    private TextField dateFinField;

    @FXML
    private Label clientErrorLabel;

    @FXML
    private Label prestataireErrorLabel;

    @FXML
    private Label dateDebutErrorLabel;

    @FXML
    private Label dateFinErrorLabel;

    @FXML
    private Button viewReservatioButtonID;

    private Map<String, Integer> clientMap = new HashMap<>();
    private Map<String, Integer> prestataireMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialisation des listes déroulantes avec les données de la base
        loadClientsFromDatabase();
        loadPrestatairesFromDatabase();

        // Effacer les messages d'erreur lorsque les champs sont modifiés
        clientField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clientErrorLabel.setText("");
            }
        });

        prestataireField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                prestataireErrorLabel.setText("");
            }
        });

        dateDebutField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                dateDebutErrorLabel.setText("");
                dateDebutField.getStyleClass().remove("error");
            }
        });

        dateFinField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                dateFinErrorLabel.setText("");
                dateFinField.getStyleClass().remove("error");
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

        // Validation des champs
        if (clientField.getSelectionModel().isEmpty()) {
            clientErrorLabel.setText("Veuillez sélectionner un client.");
            hasError = true;
        }

        if (prestataireField.getSelectionModel().isEmpty()) {
            prestataireErrorLabel.setText("Veuillez sélectionner un prestataire.");
            hasError = true;
        }

        if (dateDebutField.getText().trim().isEmpty()) {
            dateDebutErrorLabel.setText("Veuillez entrer la date de début.");
            dateDebutField.getStyleClass().add("error");
            hasError = true;
        }

        if (dateFinField.getText().trim().isEmpty()) {
            dateFinErrorLabel.setText("Veuillez entrer la date de fin.");
            dateFinField.getStyleClass().add("error");
            hasError = true;
        }

        if (!hasError) {
            try (Connection connection = MysqlConnection.getDBConnection()) {
                // Récupérer les IDs sélectionnés
                int clientId = clientMap.get(clientField.getSelectionModel().getSelectedItem());
                int prestataireId = prestataireMap.get(prestataireField.getSelectionModel().getSelectedItem());

                // Insérer la réservation
                String sql = "INSERT INTO reservation (client_id, prestataire_id, date_debut, date_fin) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, clientId);
                ps.setInt(2, prestataireId);
                ps.setString(3, dateDebutField.getText());
                ps.setString(4, dateFinField.getText());
                ps.execute();

                System.out.println("Réservation ajoutée avec succès.");
                navigateToReservationTable();

            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
            }
        }
    }

    @FXML
    void viewReservationButton(ActionEvent event) {
        navigateToReservationTable();
    }

    private void navigateToReservationTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservationTable.fxml"));
            HBox root = loader.load();
            Stage stage = (Stage) ReserverButtonId.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClientsFromDatabase() {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String query = "SELECT client_id, CONCAT(nom, ' ', prenom) AS fullName FROM client";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            clientField.getItems().clear();
            while (rs.next()) {
                int clientId = rs.getInt("client_id");
                String fullName = rs.getString("fullName");
                clientField.getItems().add(fullName);
                clientMap.put(fullName, clientId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des clients : " + e.getMessage());
        }
    }

    private void loadPrestatairesFromDatabase() {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String query = "SELECT prestataire_id, CONCAT(nom, ' ', prenom) AS fullName FROM prestataire";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            prestataireField.getItems().clear();
            while (rs.next()) {
                int prestataireId = rs.getInt("prestataire_id");
                String fullName = rs.getString("fullName");
                prestataireField.getItems().add(fullName);
                prestataireMap.put(fullName, prestataireId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des prestataires : " + e.getMessage());
        }
    }
}
