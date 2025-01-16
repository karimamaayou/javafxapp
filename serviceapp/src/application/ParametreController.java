package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParametreController {

    @FXML
    private Button addButtonmetierID;

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

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javafx_project_bd";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @FXML
    void addMetier(ActionEvent event) {
        String metier = metierField.getText().trim();

        if (metier.isEmpty()) {
            metierErrorLabel.setText("Le champ 'M�tier' est vide !");
            metierErrorLabel.setStyle("-fx-text-fill: red;"); // Texte rouge pour les erreur
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // V�rification si le m�tier existe d�j�
            String checkSql = "SELECT COUNT(*) FROM metier WHERE metier = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, metier);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                metierErrorLabel.setText("Le m�tier existe d�j� !");
                metierErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Insertion du m�tier
            String insertSql = "INSERT INTO metier (metier) VALUES (?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, metier);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                metierErrorLabel.setText("M�tier ajout� avec succ�s !");
                metierErrorLabel.setStyle("-fx-text-fill: green;"); // Texte vert pour le succ�s
            }
        } catch (SQLException e) {
            metierErrorLabel.setText("Erreur lors de l'insertion du m�tier.");
            metierErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    void addVille(ActionEvent event) {
        String ville = villeField.getText().trim();

        if (ville.isEmpty()) {
            villeErrorLabel.setText("Le champ 'Ville' est vide !");
            villeErrorLabel.setStyle("-fx-text-fill: red;"); // Texte rouge pour les erreurs
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // V�rification si la ville existe d�j�
            String checkSql = "SELECT COUNT(*) FROM ville WHERE ville = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, ville);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                villeErrorLabel.setText("La ville existe d�j� !");
                villeErrorLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Insertion de la ville
            String insertSql = "INSERT INTO ville (ville) VALUES (?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, ville);
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted > 0) {
                villeErrorLabel.setText("Ville ajout�e avec succ�s !");
                villeErrorLabel.setStyle("-fx-text-fill: green;"); // Texte vert pour le succ�s
            }
        } catch (SQLException e) {
            villeErrorLabel.setText("Erreur lors de l'insertion de la ville.");
            villeErrorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}
