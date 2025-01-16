package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class ParametreController {

    @FXML
    private Button addButtonmetierID;
    @FXML
    private Button changerButtonID;
    @FXML
    private Button addButtonvilleID;

    @FXML
    private Label metierErrorLabel;

    @FXML
    private TextField metierField;

    @FXML
    private Label villeErrorLabel;

    @FXML
    private TextField villeField;
    
    @FXML
    private TextField newPassword;
    @FXML
    private Label newPasswordError;
    @FXML
    private TextField passwordConfirmation;
    @FXML
    private Label passwordConfirmationError;
    @FXML
    private Label successMessage;

    @FXML
    public void initialize() {
        // Ajout de listeners pour effacer les messages d'erreur
        metierField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                metierErrorLabel.setText("");
                metierField.getStyleClass().remove("error"); // Supprime une classe CSS (optionnel)
            }
        });

        villeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                villeErrorLabel.setText("");
                villeField.getStyleClass().remove("error"); // Supprime une classe CSS (optionnel)
            }
        });
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
            	newPasswordError.setText("");
                newPassword.getStyleClass().remove("error"); // Supprime une classe CSS (optionnel)
            }
        });
        passwordConfirmation.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
            	passwordConfirmationError.setText("");
                passwordConfirmation.getStyleClass().remove("error"); // Supprime une classe CSS (optionnel)
            }
        });
    }

    @FXML
    void addMetier(ActionEvent event) {
        String metier = metierField.getText().trim();

        if (metier.isEmpty()) {
            metierErrorLabel.setText("Le champ 'Métier' est vide !");
            metierErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection connection = MysqlConnection.getDBConnection()) {
            if (connection == null) {
                metierErrorLabel.setText("Erreur de connexion à la base de données.");
                metierErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String checkSql = "SELECT COUNT(*) FROM metier WHERE metier = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, metier);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                metierErrorLabel.setText("Le métier existe déjà !");
                metierErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String insertSql = "INSERT INTO metier (metier) VALUES (?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, metier);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                metierErrorLabel.setText("Métier ajouté avec succès !");
                metierErrorLabel.setStyle("-fx-text-fill: green;");
            }
        } catch (SQLException e) {
            metierErrorLabel.setText("Erreur lors de l'insertion du métier.");
            metierErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    void addVille(ActionEvent event) {
        String ville = villeField.getText().trim();

        if (ville.isEmpty()) {
            villeErrorLabel.setText("Le champ 'Ville' est vide !");
            villeErrorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection connection = MysqlConnection.getDBConnection()) {
            if (connection == null) {
                villeErrorLabel.setText("Erreur de connexion à la base de données.");
                villeErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String checkSql = "SELECT COUNT(*) FROM ville WHERE ville = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, ville);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                villeErrorLabel.setText("La ville existe déjà !");
                villeErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            String insertSql = "INSERT INTO ville (ville) VALUES (?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, ville);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                villeErrorLabel.setText("Ville ajoutée avec succès !");
                villeErrorLabel.setStyle("-fx-text-fill: green;");
            }
        } catch (SQLException e) {
            villeErrorLabel.setText("Erreur lors de l'insertion de la ville.");
            villeErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
    

    @FXML
    void changePassword(ActionEvent event) {
    	String password = newPassword.getText().trim();
    	String confirmationPassword = passwordConfirmation.getText().trim();
    	
        if (confirmationPassword.isEmpty() && password.isEmpty()) {
        	newPasswordError.setText("Le champ est vide !");
        	newPasswordError.setStyle("-fx-text-fill: red;");
        	passwordConfirmationError.setText("Le champ est vide !");
        	passwordConfirmationError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (password.isEmpty()) {
        	newPasswordError.setText("Le champ est vide !");
        	newPasswordError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (confirmationPassword.isEmpty()) {
        	passwordConfirmationError.setText("Le champ est vide !");
        	passwordConfirmationError.setStyle("-fx-text-fill: red;");
            return;
        }
        // Check password length (for example, minimum 8 characters)
        if (password.length() < 8) {
            newPasswordError.setText("Le mot de passe est trop court!");
            newPasswordError.setStyle("-fx-text-fill: red;");
            passwordConfirmationError.setText("");
            return;
        }
        // Check if passwords match
        if (!password.equals(confirmationPassword)) {
            passwordConfirmationError.setText("Mots de passe différents!");
            passwordConfirmationError.setStyle("-fx-text-fill: red;");
            newPasswordError.setText("Mots de passe différents!");
            newPasswordError.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String hashedPassword = BCrypt.hashpw(confirmationPassword, BCrypt.gensalt());
        
        try (Connection connection = MysqlConnection.getDBConnection()) {
        	
        	String updatePassword = "update user set password = ? ";
        	PreparedStatement updateStatement = connection.prepareStatement(updatePassword);
        	updateStatement.setString(1, hashedPassword);
        	updateStatement.executeUpdate();
        	successMessage.setText("Le mot de passe a été changé !");
        	successMessage.setStyle("-fx-text-fill: green;");
        	
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
   

    }
}
