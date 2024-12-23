
package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class AddClientController { 
	  @FXML
	    private Label villeErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;
    

    @FXML
    private Button viewClientsButtonID;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private TextField phoneField;
	    	    
	@FXML
	
	void viewClientsButton(ActionEvent event) {
		try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("clientsTable.fxml"));
            HBox root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) viewClientsButtonID.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

	    }
    @FXML
    private ChoiceBox<String> villeField;

    @FXML
    private Button addButtonID;

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
    }

   
    @FXML
    private void addClient(ActionEvent event) {
        
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
        

       
    }
   


}
