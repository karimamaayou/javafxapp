package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AddPrestataireController {
	@FXML
	private ComboBox<String> M�tierField;

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
	private ComboBox<String> villeField;
	@FXML
	private Label m�tierErrorLabel;
	@FXML
	private Button addButtonID;

	@FXML
	private Label descriptionErrorLabel;
	@FXML
	private TextField descriptionField;
	


	@FXML
	private void handleAddPrestataire() {
	    String firstName = firstNameField.getText();
	    String lastName = lastNameField.getText();
	    String email = emailField.getText();
	    String phone = phoneField.getText();
	    String m�tier = M�tierField.getValue();
	    String ville = villeField.getValue();
	    String description = descriptionField.getText();
	    double tarif = Double.parseDouble(tarifField.getText());

	    boolean isValid = true;
	    // V�rification des prix
	  

	    // Si tous les champs sont valides, on ajoute le prestataire � la base de donn�es
	    if (isValid) {
	        addPrestataireToDatabase(firstName, lastName, email, phone, tarif, m�tier, ville, description);
	    }
	}


	private void addPrestataireToDatabase(String firstName, String lastName, String email, String phone, double tarif, String m�tier, String ville, String description)  {
	    try (Connection connection = MysqlConnection.getDBConnection()) {

	        // R�cup�rer l'ID du m�tier
	        int metierId = getMetierId(connection, m�tier);
	        if (metierId == -1) {
	            return;
	        }

	        // R�cup�rer l'ID de la ville
	        int villeId = getVilleId(connection, ville);
	        if (villeId == -1) {
	            return;
	        }

	        // Requ�te SQL pour ins�rer le prestataire dans la base de donn�es
	        String sql = "INSERT INTO prestataire (metier_id, nom, prenom, email, telephone, ville_id, description, tarif, disponibilite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	        // Pr�parer la requ�te SQL
	        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	            // Remplir les param�tres de la requ�te SQL
	            stmt.setInt(1, metierId); // ID du m�tier
	            stmt.setString(2, lastName); // Nom
	            stmt.setString(3, firstName); // Pr�nom
	            stmt.setString(4, email); // Email
	            stmt.setString(5, phone); // T�l�phone
	            stmt.setInt(6, villeId); // ID de la ville
	            stmt.setString(7, description); // Description
	            stmt.setDouble(8, tarif); // Tarif
	            stmt.setInt(9, 1); // Disponibilit� (1 pour disponible, 0 pour non disponible)

	            // Ex�cuter la requ�te
	            stmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    setSceneToPrestataireTable();
	}

		
	private void setSceneToPrestataireTable() {
	    try {
	        // Charger le fichier FXML de la table des prestataires
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("prestataireTable.fxml"));
	        HBox root = loader.load();

	        // Obtenir la sc�ne actuelle et la changer avec la nouvelle sc�ne
	        Stage stage = (Stage) viewPrestatireButtonID.getScene().getWindow();
	        stage.setScene(new Scene(root));

	    } catch (IOException e) {
	        e.printStackTrace();
	        showAlert("Erreur", "Une erreur s'est produite lors du changement de sc�ne.");
	    }
	}

	// M�thode pour obtenir l'ID du m�tier
	private int getMetierId(Connection connection, String m�tier) {
		try {
			String sql = "SELECT metier_id FROM metier WHERE metier = ?";
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, m�tier);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("metier_id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // Retourner -1 si le m�tier n'existe pas
	}

	// M�thode pour obtenir l'ID de la ville
	private int getVilleId(Connection connection, String ville) {
		try {
			String sql = "SELECT ville_id FROM ville WHERE ville = ?";
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, ville);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					return rs.getInt("ville_id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // Retourner -1 si la ville n'existe pas
	}

	// M�thode pour afficher une alerte
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

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
		tarifField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				priceErrorLabel.setText(""); // Efface l'erreur du label Prix
				tarifField.getStyleClass().remove("error");
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

		loadMetiersFromDatabase();
		loadVillesFromDatabase();

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
		} else if (firstNameField.getText().trim().isEmpty()) {
			nameErrorLabel.setText("Veuillez entrer votre pr�nom.");
			firstNameField.getStyleClass().add("error");
			hasError = true;
		} else if (lastNameField.getText().trim().isEmpty()) {
			nameErrorLabel.setText("Veuillez entrer votre nom.");
			lastNameField.getStyleClass().add("error");
			hasError = true;
		}

		// V�rification du champ Email avec regex
		String email = emailField.getText().trim();
		String emailRegex = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"; // Regex pour valider un email

		if (email.isEmpty()) {
		    emailErrorLabel.setText("Veuillez entrer votre email.");
		    emailField.getStyleClass().add("error");
		    hasError = true;
		} else if (!email.matches(emailRegex)) {
		    // V�rifie si l'email est valide avec regex
		    emailErrorLabel.setText("Veuillez entrer un email valide (exemple : nom@domaine.com).");
		    emailField.getStyleClass().add("error");
		    hasError = true;
		} else {
		    // Si l'email est valide
		    emailErrorLabel.setText(""); // Pas d'erreur
		    emailField.getStyleClass().remove("error");
		}


		// V�rification du champ T�l�phone
		if (phoneField.getText().trim().isEmpty()) {
		    phoneErrorLabel.setText("Veuillez entrer votre num�ro de t�l�phone.");
		    phoneField.getStyleClass().add("error");
		    hasError = true;
		} else if (!phoneField.getText().matches("\\d{10}")) { 
		    // V�rifie que le num�ro contient exactement 10 chiffres
		    phoneErrorLabel.setText("Le num�ro de t�l�phone doit contenir exactement 10 chiffres.");
		    phoneField.getStyleClass().add("error");
		    hasError = true;
		} else {
		    // Si tout est correct
		    phoneErrorLabel.setText(""); // Pas d'erreur
		    phoneField.getStyleClass().remove("error");
		}
			    
			
	
		// V�rification du champ Prix minimum
		if (tarifField.getText().trim().isEmpty() ) {
			priceErrorLabel.setText("Veuillez entrer la tarif .");
			tarifField.getStyleClass().add("error");
			hasError = true;
		} else

		
	
		
		
		
		if (villeField.getSelectionModel().getSelectedItem() == null
				|| villeField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
			villeErrorLabel.setText("Veuillez s�lectionner une ville.");
			villeField.getStyleClass().add("error");
			hasError = true;
		}

		// V�rification du m�tier
		if (M�tierField.getSelectionModel().getSelectedItem() == null
				|| M�tierField.getSelectionModel().getSelectedItem().trim().isEmpty()) {
			m�tierErrorLabel.setText("Veuillez s�lectionner un m�tier.");
			M�tierField.getStyleClass().add("error");
			hasError = true;
		}
		
		if (descriptionField.getText().trim().isEmpty()) {
			descriptionErrorLabel.setText("Veuillez s�lectionner une description.");
		}
	
		
		
		
		
	if(!hasError ) {
		handleAddPrestataire();
	}
		
	}

	// ajout� des M�tier pour M�tier en base de donne
	private void loadMetiersFromDatabase() {
		try (Connection conn = MysqlConnection.getDBConnection()) {
			String query = "SELECT metier FROM metier"; // Requ�te pour r�cup�rer les m�tiers

			try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

				M�tierField.getItems().clear(); // Vide la liste avant de la remplir avec les nouveaux �l�ments

				while (rs.next()) {
					String metier = rs.getString("metier");
					M�tierField.getItems().add(metier); // Ajoute chaque m�tier dans le ComboBox

				}

			}
		} catch (SQLException e) {
			e.printStackTrace(); // Affiche l'erreur SQL si elle survient
		}
	}
	// ajout� des ville pour les ville en base de donne

	private void loadVillesFromDatabase() {
		try (Connection conn = MysqlConnection.getDBConnection()) {
			String query = "SELECT ville FROM ville"; // Requ�te pour r�cup�rer les villes

			try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

				villeField.getItems().clear(); // Vide la liste avant de la remplir avec les nouveaux �l�ments

				while (rs.next()) {
					String ville = rs.getString("ville");
					villeField.getItems().add(ville); // Ajoute chaque ville dans le ComboBox

				}

				
			}
		} catch (SQLException e) {

		}
	}

	// chenge de page ver table clients
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

	// chenge de page ver table prestataire
	@FXML
	void viewPrestatireButton(ActionEvent event) {
		try {
			// Load the FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("prestataireTable.fxml"));
			HBox root = loader.load();
			// Get the current stage (window) and set the new scene
			Stage stage = (Stage) viewPrestatireButtonID.getScene().getWindow();
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

























