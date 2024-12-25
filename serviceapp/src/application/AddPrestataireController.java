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
	    private ChoiceBox<String> M�tierField;

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
	    private Label m�tierErrorLabel;


    @FXML
    public void initialize() {
        // �couteurs pour chaque champ TextField
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                emailErrorLabel.setText(""); // Efface l'erreur du label Email
                emailField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                nameErrorLabel.setText(""); // Efface l'erreur du label Pr�nom
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
                phoneErrorLabel.setText(""); // Efface l'erreur du label T�l�phone
                phoneField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        villeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                villeErrorLabel.setText(""); // Efface l'erreur du label Ville
                villeField.getStyleClass().remove("error"); // Supprime la classe "error" du champ
            }
        });

        // On peut ajouter des �couteurs suppl�mentaires pour les champs min/max price
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
                villeErrorLabel.setText("Veuillez s�lectionner une ville.");
                villeField.getStyleClass().add("error");
            } else {
                villeErrorLabel.setText("");
                villeField.getStyleClass().remove("error");
            }
        });

        // �couteur pour la s�lection du m�tier
        M�tierField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                m�tierErrorLabel.setText("Veuillez s�lectionner un m�tier.");
                M�tierField.getStyleClass().add("error");
            } else {
                m�tierErrorLabel.setText("");
                M�tierField.getStyleClass().remove("error");
            }
        });

    }

    
    
    
    
    @FXML
    void addPrestataire(ActionEvent event) {
        boolean hasError = false;

        // V�rification du champ de nom complet
        if (lastNameField.getText().trim().isEmpty() && firstNameField.getText().trim().isEmpty()) {
            nameErrorLabel.setText("Veuillez entrer votre nom complet.");
            firstNameField.getStyleClass().add("error");
            lastNameField.getStyleClass().add("error");
            hasError = true;
        } else 
            if (firstNameField.getText().trim().isEmpty()) {
                nameErrorLabel.setText("Veuillez entrer votre pr�nom.");
                firstNameField.getStyleClass().add("error");
                hasError = true;
            }
            else
            	if (lastNameField.getText().trim().isEmpty()) {
                nameErrorLabel.setText("Veuillez entrer votre nom.");
                lastNameField.getStyleClass().add("error");
                hasError = true;
            }
        

        // V�rification du champ Email
        if (emailField.getText().trim().isEmpty()) {
            emailErrorLabel.setText("Veuillez entrer votre email.");
            emailField.getStyleClass().add("error");
            hasError = true;
        }

        // V�rification du champ T�l�phone
        if (phoneField.getText().trim().isEmpty()) {
            phoneErrorLabel.setText("Veuillez entrer votre num�ro de t�l�phone.");
            phoneField.getStyleClass().add("error");
            hasError = true;
        }

       if(minPriceField.getText().trim().isEmpty() && maxPriceField.getText().trim().isEmpty() ) {
    	   priceErrorLabel.setText("Veuillez entrer un prix .");
           minPriceField.getStyleClass().add("error");
           maxPriceField.getStyleClass().add("error");
           hasError = true;
       } else
        // V�rification du champ Prix minimum
        if (minPriceField.getText().trim().isEmpty()) {
            priceErrorLabel.setText("Veuillez entrer un prix minimum.");
            minPriceField.getStyleClass().add("error");
            hasError = true;
        } else

        // V�rification du champ Prix maximum
        if (maxPriceField.getText().trim().isEmpty()) {
            priceErrorLabel.setText("Veuillez entrer un prix maximum.");
            maxPriceField.getStyleClass().add("error");
            hasError = true;
        }
        if (villeField.getSelectionModel().getSelectedItem() == null || villeField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
            villeErrorLabel.setText("Veuillez s�lectionner une ville.");
            villeField.getStyleClass().add("error");
            hasError = true;
        }

        // V�rification du m�tier
        if (M�tierField.getSelectionModel().getSelectedItem() == null || M�tierField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
            m�tierErrorLabel.setText("Veuillez s�lectionner un m�tier.");
            M�tierField.getStyleClass().add("error");
            hasError = true;
        }

    
    
    
    
}}