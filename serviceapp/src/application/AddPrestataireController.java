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

public class AddPrestataireController {
	 @FXML
	    private ChoiceBox<String> MétierField;

	    @FXML
	    private Label emailErrorLabel;

	    @FXML
	    private TextField emailField;

	    @FXML
	    private TextField firstNameField;

	    @FXML
	    private TextField lastNameField;

	    @FXML
	    private TextField maxPriceField;

	    @FXML
	    private TextField minPriceField;

	    @FXML
	    private Label nameErrorLabel;

	    @FXML
	    private Label phoneErrorLabel;

	    @FXML
	    private TextField phoneField;

	    @FXML
	    private Label priceErrorLabel;

	    @FXML
	    private Label titleErrorLabel;

	    @FXML
	    private Button viewClientsButtonID;

	    @FXML
	    private Button viewPrestatireButtonID;

	    @FXML
	    private Label villeErrorLabel;

	    @FXML
	    private ChoiceBox<String> villeField;
	    @FXML
	    private Label métierErrorLabel;


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
            if (newValue != null && !newValue.trim().isEmpty()) {
                villeErrorLabel.setText(""); // Efface l'erreur du label Ville
                villeField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        // On peut ajouter des écouteurs supplémentaires pour les champs min/max price
        minPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                priceErrorLabel.setText(""); // Efface l'erreur du label Prix
                minPriceField.getStyleClass().remove("error");
            }
        });

        maxPriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                priceErrorLabel.setText(""); // Efface l'erreur du label Prix
                maxPriceField.getStyleClass().remove("error");
            }
        });

        villeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                villeErrorLabel.setText("Veuillez sélectionner une ville.");
                villeField.getStyleClass().add("error");
            } else {
                villeErrorLabel.setText("");
                villeField.getStyleClass().remove("error");
            }
        });

        // Écouteur pour la sélection du métier
        MétierField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                métierErrorLabel.setText("Veuillez sélectionner un métier.");
                MétierField.getStyleClass().add("error");
            } else {
                métierErrorLabel.setText("");
                MétierField.getStyleClass().remove("error");
            }
        });

    }

    
    
    
    
    @FXML
    void addPrestataire(ActionEvent event) {
        boolean hasError = false;

        // Vérification du champ de nom complet
        if (lastNameField.getText().trim().isEmpty() && firstNameField.getText().trim().isEmpty()) {
            nameErrorLabel.setText("Veuillez entrer votre nom complet.");
            firstNameField.getStyleClass().add("error");
            lastNameField.getStyleClass().add("error");
            hasError = true;
        } else 
            if (firstNameField.getText().trim().isEmpty()) {
                nameErrorLabel.setText("Veuillez entrer votre prénom.");
                firstNameField.getStyleClass().add("error");
                hasError = true;
            }
            else
            	if (lastNameField.getText().trim().isEmpty()) {
                nameErrorLabel.setText("Veuillez entrer votre nom.");
                lastNameField.getStyleClass().add("error");
                hasError = true;
            }
        

        // Vérification du champ Email
        if (emailField.getText().trim().isEmpty()) {
            emailErrorLabel.setText("Veuillez entrer votre email.");
            emailField.getStyleClass().add("error");
            hasError = true;
        }

        // Vérification du champ Téléphone
        if (phoneField.getText().trim().isEmpty()) {
            phoneErrorLabel.setText("Veuillez entrer votre numéro de téléphone.");
            phoneField.getStyleClass().add("error");
            hasError = true;
        }

       if(minPriceField.getText().trim().isEmpty() && maxPriceField.getText().trim().isEmpty() ) {
    	   priceErrorLabel.setText("Veuillez entrer un prix .");
           minPriceField.getStyleClass().add("error");
           maxPriceField.getStyleClass().add("error");
           hasError = true;
       } else
        // Vérification du champ Prix minimum
        if (minPriceField.getText().trim().isEmpty()) {
            priceErrorLabel.setText("Veuillez entrer un prix minimum.");
            minPriceField.getStyleClass().add("error");
            hasError = true;
        } else

        // Vérification du champ Prix maximum
        if (maxPriceField.getText().trim().isEmpty()) {
            priceErrorLabel.setText("Veuillez entrer un prix maximum.");
            maxPriceField.getStyleClass().add("error");
            hasError = true;
        }
        if (villeField.getSelectionModel().getSelectedItem() == null || villeField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
            villeErrorLabel.setText("Veuillez sélectionner une ville.");
            villeField.getStyleClass().add("error");
            hasError = true;
        }

        // Vérification du métier
        if (MétierField.getSelectionModel().getSelectedItem() == null || MétierField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
            métierErrorLabel.setText("Veuillez sélectionner un métier.");
            MétierField.getStyleClass().add("error");
            hasError = true;
        }

    
    
    
    
}}