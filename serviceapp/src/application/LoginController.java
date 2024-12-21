package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController<ActionEvent> {

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_Password;

    @FXML
    private Label error_label;

    @FXML
    private Label email_error_label;

    @FXML
    private Label password_error_label;

    public void initialize() {
        // Listener pour le champ Email
        tf_email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                email_error_label.setText(""); // Efface l'erreur du label Email
                tf_email.getStyleClass().remove("error"); // Supprime la classe "error" du champ
                error_label.setText("");
            }
        });

        // Listener pour le champ Mot de passe
        tf_Password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                password_error_label.setText(""); // Efface l'erreur du label Mot de passe
                tf_Password.getStyleClass().remove("error"); // Supprime la classe "error" du champ
                error_label.setText(""); // Efface l'erreur générale
            }
        });
    }

    // Cette méthode est appelée lors du clic sur le bouton de connexion
    @FXML
    private void handleLogin(javafx.event.ActionEvent event) throws IOException {
        String email = tf_email.getText().trim(); // Remplacé username par email
        String password = tf_Password.getText().trim();
        boolean hasError = false;

        // Vérification du champ Email
        if (email.isEmpty()) {
            email_error_label.setText("Veuillez entrer votre email.");
            tf_email.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }

        // Vérification du champ Mot de passe
        if (password.isEmpty()) {
            password_error_label.setText("Veuillez entrer votre mot de passe.");
            tf_Password.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }

        // Si aucun champ n'a d'erreur, valider les informations de connexion
        if (!hasError) {
            if (validateLogin(email, password)) { // Utilisation de email
                DBUtils.changeScene( event, "clientsTable.fxml", "Page d'accueil", email, null); // Utilisation de email
            } else {
                error_label.setText("Email ou mot de passe incorrect.");
                tf_email.getStyleClass().add("error"); // Ajout de la classe "error" au champ email
                tf_Password.getStyleClass().add("error"); // Ajout de la classe "error" au champ mot de passe
            }
        }
    }


    private boolean validateLogin(String email, String password) {
        boolean isValid = false;

        try (Connection conn = MysqlConnection.getDBConnection()) {
        	if (conn != null) {
                System.out.println("Database connected.");
            } else {
                System.out.println("Failed to connect to the database.");
                return false;
            }
            String sql = "SELECT * FROM user WHERE email = ? AND Password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet resultSet = stmt.executeQuery();
            System.out.println(resultSet);
            if (resultSet.next()) {
                isValid = false; 
                System.out.println("availible");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }

}
