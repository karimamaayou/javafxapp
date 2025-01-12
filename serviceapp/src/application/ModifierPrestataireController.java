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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifierPrestataireController {

    @FXML
    private Button addButtonID;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;


    @FXML
    private TextField tarifField;

    @FXML
    private Label métierErrorLabel;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private Button viewClientsButtonID;

    @FXML
    private Button viewPrestataireButtonID;

    @FXML
    private Label villeErrorLabel;

    @FXML
    private ComboBox<String> MétierField;

    @FXML
    private ComboBox<String> villeField;

    private Prestataire selectedPrestataire;

    private Map<String, Integer> villeMap = new HashMap<>();
    private Map<String, Integer> metierMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupFieldListeners();
        loadVillesFromDatabase();
        loadMetiersFromDatabase();
    }

    @FXML
    void modifierPrestataire(ActionEvent event) {
        if (!validateFields()) {
            updatePrestataireInDatabase();
            closeWindow();
        }
    }

    private void setupFieldListeners() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> clearError(emailErrorLabel, emailField));
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> clearError(nameErrorLabel, firstNameField));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> clearError(nameErrorLabel, lastNameField));
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> clearError(phoneErrorLabel, phoneField));
        tarifField.textProperty().addListener((observable, oldValue, newValue) -> clearError(priceErrorLabel, tarifField));
        villeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> clearError(villeErrorLabel, villeField));
        MétierField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> clearError(métierErrorLabel, MétierField));
    }

    private boolean validateFields() {
        boolean hasError = false;

        if (isFieldEmpty(firstNameField) || isFieldEmpty(lastNameField)) {
            setError(nameErrorLabel, "Veuillez entrer votre nom complet.", firstNameField, lastNameField);
            hasError = true;
        }

        if (isFieldEmpty(emailField)) {
            setError(emailErrorLabel, "Veuillez entrer votre email.", emailField);
            hasError = true;
        }

        if (isFieldEmpty(phoneField)) {
            setError(phoneErrorLabel, "Veuillez entrer votre numéro de téléphone.", phoneField);
            hasError = true;
        }

        if (isFieldEmpty(tarifField) ) {
            setError(priceErrorLabel, "Veuillez entrer la tarif .", tarifField);
            hasError = true;
        }

        if (villeField.getSelectionModel().getSelectedItem() == null) {
            setError(villeErrorLabel, "Veuillez sélectionner une ville.", villeField);
            hasError = true;
        }

        if (MétierField.getSelectionModel().getSelectedItem() == null) {
            setError(métierErrorLabel, "Veuillez sélectionner un métier.", MétierField);
            hasError = true;
        }

        if (isFieldEmpty(descriptionField)) {
            setError(descriptionErrorLabel, "Veuillez entrer une description.", descriptionField);
            hasError = true;
        }

        return hasError;
    }

    private void setError(Label label, String message, TextField... fields) {
        label.setText(message);
        for (TextField field : fields) {
            field.getStyleClass().add("error");
        }
    }

    private void setError(Label label, String message, ComboBox<?> comboBox) {
        label.setText(message);
        if (!comboBox.getStyleClass().contains("error")) {
            comboBox.getStyleClass().add("error");
        }
    }

    private void updatePrestataireInDatabase() {
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String sql = "UPDATE prestataire SET metier_id = ?, nom = ?, prenom = ?, email = ?, telephone = ?, ville_id = ?, description = ?,tarif = ? WHERE prestataire_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, metierMap.get(MétierField.getSelectionModel().getSelectedItem()));
                ps.setString(2, lastNameField.getText());
                ps.setString(3, firstNameField.getText());
                ps.setString(4, emailField.getText());
                ps.setString(5, phoneField.getText());
                ps.setInt(6, villeMap.get(villeField.getSelectionModel().getSelectedItem()));
                ps.setString(7, descriptionField.getText());
                ps.setDouble(8, Double.parseDouble(tarifField.getText()));
                ps.setInt(9, selectedPrestataire.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadVillesFromDatabase() {
        loadDataFromDatabase("SELECT ville_id, ville FROM ville", villeField, villeMap);
    }

    private void loadMetiersFromDatabase() {
        loadDataFromDatabase("SELECT metier_id, metier FROM metier", MétierField, metierMap);
    }

    private void loadDataFromDatabase(String query, ComboBox<String> comboBox, Map<String, Integer> map) {
        try (Connection conn = MysqlConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            comboBox.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                comboBox.getItems().add(name);
                map.put(name, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    private boolean isFieldEmpty(TextField field) {
        return field.getText().trim().isEmpty();
    }

    private void clearError(Label label, TextField field) {
        label.setText("");
        field.getStyleClass().remove("error");
    }

    private void clearError(Label label, ComboBox<String> comboBox) {
        label.setText("");
        comboBox.getStyleClass().remove("error");
    }

    public void setSelectedPrestataire(Prestataire prestataire) {
        this.selectedPrestataire = prestataire;

        // Pré-remplir les champs avec les informations du prestataire sélectionné
        firstNameField.setText(prestataire.getNom());
        lastNameField.setText(prestataire.getPrenom());
        emailField.setText(prestataire.getEmail());
        phoneField.setText(prestataire.getTelephone());
        descriptionField.setText(prestataire.getDescription());
        tarifField.setText(String.valueOf(prestataire.getTarif()));
       

  }
}
