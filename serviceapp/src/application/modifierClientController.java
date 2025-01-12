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

public class ModifierClientController {

    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Button modifierButtonID;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private TextField phoneField;

    @FXML
    private Label villeErrorLabel;

    @FXML
    private ComboBox<String> villeField;
    
    private Client selectedClient; // we will use this variable to hold the selectClient so we can use in the rest of the code
    private Map<String, Integer> villeMap = new HashMap<>();// list pour affecter les id ou villes corespondant
    @FXML
    public void initialize() {
        // Écouteurs pour chaque champ TextField
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                emailErrorLabel.setText(""); // Efface l'erreur du label Email
                emailField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                nameErrorLabel.setText(""); // Efface l'erreur du label Prénom
                firstNameField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                nameErrorLabel.setText(""); // Efface l'erreur du label Nom
                lastNameField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                phoneErrorLabel.setText(""); // Efface l'erreur du label Téléphone
                phoneField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        villeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Réinitialise les erreurs si une ville est sélectionnée
            }
        });
        villeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                villeErrorLabel.setText(""); // Efface l'erreur du label Ville
                villeField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });
    	
    	loadMetiersFromDatabase();
    }
    
    public void setSelectedClient(Client client) {
        this.selectedClient = client;
        
        
        lastNameField.setText(client.getNom());
        firstNameField.setText(client.getPrenom());
        phoneField.setText(client.telephone);
        emailField.setText(client.email);
        
       
    }

    @FXML
    void modifierClient(ActionEvent event) {
        boolean hasError = false;
     // Vérification du champde le nom comple
        if (lastNameField.getText().trim().isEmpty()  && firstNameField.getText().trim().isEmpty()  ) {
        	nameErrorLabel.setText("Veuillez entrer votre nom comple.");
        	 firstNameField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
             lastNameField.getStyleClass().add("error"); ; // Ajoute la classe "error" au champ
             hasError = true;
        } else 
        	
        	if (firstNameField.getText().trim().isEmpty()) {
            nameErrorLabel.setText("Veuillez entrer votre prénom.");
            firstNameField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }   else
        	
        	if (lastNameField.getText().trim().isEmpty()) {
            nameErrorLabel.setText("Veuillez entrer votre nom.");
            lastNameField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }
        
        
        
        // Vérification du champ Email
        if (emailField.getText().trim().isEmpty()) {
            emailErrorLabel.setText("Veuillez entrer votre email.");
            emailField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }

       
       

        // Vérification du champ Nom
       

        // Vérification du champ Téléphone
        if (phoneField.getText().trim().isEmpty()) {
            phoneErrorLabel.setText("Veuillez entrer votre numéro de téléphone.");
            phoneField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        }

        // Vérification du champ Ville
        if (villeField.getSelectionModel().isEmpty()) {
            hasError = true;
        }
        if (villeField.getSelectionModel().isEmpty()) {
            villeErrorLabel.setText("Veuillez sélectionner une ville.");
            villeField.getStyleClass().add("error"); // Ajoute la classe "error" au champ
            hasError = true;
        } 
        
        if (!hasError) {
    	//update the selected client attibutes(data) with the new modified fields
    	selectedClient.setNom(lastNameField.getText());
    	selectedClient.setPrenom(firstNameField.getText());
    	selectedClient.setTelephone(phoneField.getText());
    	selectedClient.setEmail(emailField.getText());
    	
    	
    	String villeName= villeField.getSelectionModel().getSelectedItem();
    	int ville_id=villeMap.get(villeName);
    	
    	
    	
    	
        // Update the database
        try (Connection connection = MysqlConnection.getDBConnection()) {
            String sql = "UPDATE client SET nom = ?, prenom = ?, email = ?,ville_id = ?, telephone = ? WHERE client_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, selectedClient.getNom());
            ps.setString(2, selectedClient.getPrenom());
            ps.setString(3, selectedClient.getEmail());
            ps.setInt(4, ville_id);
            ps.setString(5, selectedClient.getTelephone());
            ps.setInt(6, selectedClient.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Close the window after updating
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();

        }
    }
    
    private void loadMetiersFromDatabase() {
    	
        try (Connection conn = MysqlConnection.getDBConnection()) {
            String query = "SELECT ville_id,ville FROM ville"; // Requête pour récupérer les métiers

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

            	villeField.getItems().clear(); // Vide la liste avant de la remplir avec les nouveaux éléments

                while (rs.next()) {
                	int ville_id = rs.getInt("ville_id");
                    String ville = rs.getString("ville");
                    villeField.getItems().add(ville); // Ajoute chaque métier dans le ComboBox
                    villeMap.put(ville,ville_id);
                }

                System.out.println("Métiers chargés avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si elle survient
        }
    }

}
