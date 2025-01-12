package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class modifierReservationController {

    @FXML
    private ComboBox<String> clientField;

    @FXML
    private ComboBox<String> prestataireField;

    @FXML
    private TextField PriceField;

    @FXML
    private DatePicker dateDebutField;

    @FXML
    private DatePicker dateFinField;

    @FXML
    private Label clientErrorLabel;

    @FXML
    private Label prestataireErrorLabel;

    @FXML
    private Label dateDebutErrorLabel;

    @FXML
    private Label dateFinErrorLabel;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private Button modifierButtonId;

    private Map<String, Integer> clientMap = new HashMap<>();
    private Map<String, Integer> prestataireMap = new HashMap<>();
    private Reservation selectedReservation;

    @FXML
    public void initialize() {
        // Ajouter des listeners pour supprimer les erreurs dynamiquement
        clientField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) clientErrorLabel.setText("");
        });

        prestataireField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) prestataireErrorLabel.setText("");
        });

        dateDebutField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateDebutErrorLabel.setText("");
                dateDebutField.getStyleClass().remove("error");
            }
        });

        dateFinField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dateFinErrorLabel.setText("");
                dateFinField.getStyleClass().remove("error");
            }
        });

        PriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) priceErrorLabel.setText("");
        });

        // Charger les données depuis la base de données
        loadClientFromDatabase();
        loadPrestataireFromDatabase();
    }

    @FXML
    void modifierReservation(ActionEvent event) {
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
            dateDebutField.getStyleClass().add("error");
            hasError = true;
        }

        if (dateFinField.getValue() == null) {
            dateFinErrorLabel.setText("Veuillez entrer la date de fin.");
            dateFinField.getStyleClass().add("error");
            hasError = true;
        }

        try {
            double price = Double.parseDouble(PriceField.getText());
            if (price <= 0) {
                priceErrorLabel.setText("Le prix doit être supérieur à 0.");
                hasError = true;
            }
        } catch (NumberFormatException e) {
            priceErrorLabel.setText("Veuillez entrer un prix valide.");
            hasError = true;
        }

        // Si aucune erreur, effectuer la mise à jour
        if (!hasError) {
            try (Connection connection = MysqlConnection.getDBConnection()) {
                String sql = "UPDATE reservation SET client_id = ?, prestataire_id = ?, date_debut = ?, date_fin = ?, prix = ? WHERE reservation_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);

                int selectedClientId = clientMap.get(clientField.getSelectionModel().getSelectedItem());
                int selectedPrestataireId = prestataireMap.get(prestataireField.getSelectionModel().getSelectedItem());

                ps.setInt(1, selectedClientId);
                ps.setInt(2, selectedPrestataireId);
                ps.setString(3, dateDebutField.getValue().toString());
                ps.setString(4, dateFinField.getValue().toString());
                ps.setDouble(5, Double.parseDouble(PriceField.getText()));
                ps.setInt(6, selectedReservation.getIdReservtion());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Réservation modifiée avec succès.");
                    Stage stage = (Stage) dateDebutField.getScene().getWindow();
                    stage.close();
                } else {
                    System.err.println("Aucune réservation n'a été modifiée.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            }
        }
    }

    private void loadClientFromDatabase() {
        try (Connection conn = MysqlConnection.getDBConnection()) {
            String query = "SELECT client_id, CONCAT(nom, ' ', prenom) AS nomCompleteClient FROM client";
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                clientField.getItems().clear();
                while (rs.next()) {
                    int clientId = rs.getInt("client_id");
                    String nomCompleteClient = rs.getString("nomCompleteClient");
                    clientField.getItems().add(nomCompleteClient);
                    clientMap.put(nomCompleteClient, clientId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPrestataireFromDatabase() {
        try (Connection conn = MysqlConnection.getDBConnection()) {
            String query = "SELECT prestataire_id, CONCAT(nom, ' ', prenom) AS nomCompletePrestataire FROM prestataire";
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                prestataireField.getItems().clear();
                while (rs.next()) {
                    int prestataireId = rs.getInt("prestataire_id");
                    String nomCompletePrestataire = rs.getString("nomCompletePrestataire");
                    prestataireField.getItems().add(nomCompletePrestataire);
                    prestataireMap.put(nomCompletePrestataire, prestataireId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSelectedReservation(Reservation selectedReservation) {
        this.selectedReservation = selectedReservation;
        dateDebutField.setValue(java.time.LocalDate.parse(selectedReservation.getDateDebut()));
        dateFinField.setValue(java.time.LocalDate.parse(selectedReservation.getDateFin()));
        PriceField.setText(String.valueOf(selectedReservation.getPrix()));
    }
}
