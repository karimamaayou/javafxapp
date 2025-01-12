package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PrestataireTableController {

	@FXML
	private Button ajouterButtonID;

	@FXML
	private TableColumn<Prestataire, String> descriptionColumn;

	@FXML
	private TableColumn<Prestataire, String> disponibiliteColumn;

	@FXML
	private TableColumn<Prestataire, String> emailColumn;

	@FXML
	private Button modifierButtonID;
	
    @FXML
    private Button logOutButtonID;

	@FXML
	private TableColumn<Prestataire, String> nomColumn;

	@FXML
	private TableColumn<Prestataire, String> prenomColumn;

	@FXML
	private TableView<Prestataire> prestataireTable;

	

	@FXML
	private TableColumn<Prestataire, String> tarifColumn;

	@FXML
	private Button supprimerButtonID;

	@FXML
	private TableColumn<Prestataire, String> telephonePrestataireColumn;

	@FXML
	private TableColumn<Prestataire, String> villeColumn;

	@FXML
	private Button viewClientsButtonID;

	@FXML
	private Button viewPrestatireButtonID;
	
    @FXML
    private Button viewReservationsButtonID;
    
	@FXML
	private Button btn_recherche;

	private ObservableList<Prestataire> prestatireList = FXCollections.observableArrayList();
	
	@FXML
	private TableColumn<Prestataire, String> metierColumn;

	@FXML
	private TextField tf_recherche;

	@FXML
	private void initialize() {
		// Configure les colonnes
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
		prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		telephonePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
		villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
		metierColumn.setCellValueFactory(new PropertyValueFactory<>("metier"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		disponibiliteColumn.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
		tarifColumn.setCellValueFactory(new PropertyValueFactory<>("tarif"));
		

		// Charger tous les prestataires
		getAllPrestataire();

		
		
		
		if (tarifColumn == null) {
		    System.out.println("tarifColumn est null");
		}
		// Ajouter un listener au champ de recherche
		tf_recherche.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				loadAllPrestataires();
			} else {
				searchPrestataires(newValue);
			}
		});
	}

	public void getAllPrestataire() {
		prestatireList.clear();

		// Connexion à la base de données
		Connection connection = MysqlConnection.getDBConnection();

		// Requête SQL corrigée pour éviter les ambiguïtés et récupérer les noms des
		// villes et des métiers
		String sql = "SELECT p.prestataire_id, m.metier, p.nom, p.prenom, p.email, v.ville, p.telephone, p.description, p.disponibilite, p.tarif "
				+ "FROM prestataire p " + "JOIN ville v ON p.ville_id = v.ville_id "
				+ "JOIN metier m ON p.metier_id = m.metier_id";

		try {
			// Préparer et exécuter la requête
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet results = ps.executeQuery();

			// Parcourir les résultats de la requête    
			while (results.next()) {
				int id = results.getInt("prestataire_id");
				String metier = results.getString("metier");
				String nom = results.getString("nom");
				String prenom = results.getString("prenom");
				String email = results.getString("email");
				String ville = results.getString("ville");
				String telephone = results.getString("telephone");
				String description = results.getString("description");
				boolean disponibilite = results.getBoolean("disponibilite");
				double tarif = results.getDouble("tarif");
			

				// Ajouter l'objet Prestataire à la liste
				prestatireList.add(new Prestataire(id, metier, nom, prenom, email, ville, telephone, description,
						disponibilite  ? "Disponible":"Indisponible",tarif
					));
			}

			// Mettre à jour la TableView avec la liste des prestataires
			prestataireTable.setItems(prestatireList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// la rechaire des valeur pour le table de prestataire
	@FXML
	private void handleSearch(ActionEvent event) {
		String searchText = tf_recherche.getText(); // Récupérer la valeur entrée par l'utilisateur

		if (!searchText.isEmpty()) {
			// Effectuer la recherche dans la base de données
			searchPrestataires(searchText);
		} else {
			// Si le champ de recherche est vide, vous pouvez afficher tous les prestataires
			loadAllPrestataires();
		}
	}

	private void searchPrestataires(String searchText) {
		prestatireList.clear();
		Connection connection = MysqlConnection.getDBConnection();

		String sql = "SELECT p.prestataire_id, m.metier, p.nom, p.prenom, p.email, v.ville, "
				+ "p.telephone, p.description, p.disponibilite , p.tarif " 
				+ "FROM prestataire p "
				+ "JOIN ville v ON p.ville_id = v.ville_id "
				+ "JOIN metier m ON p.metier_id = m.metier_id "
				+ "WHERE m.metier LIKE ? OR p.nom LIKE ? OR p.prenom LIKE ? OR p.email LIKE ? "
				+ "OR v.ville LIKE ? OR p.telephone LIKE ? OR p.description LIKE ? "
				+ "OR p.disponibilite LIKE ? OR p.tarif LIKE ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			String searchPattern = "%" + searchText + "%";
			for (int i = 1; i <= 9; i++) {
				ps.setString(i, searchPattern);
			}

			ResultSet results = ps.executeQuery();
			while (results.next()) {
				int id = results.getInt("prestataire_id");
				String metier = results.getString("metier");
				String nom = results.getString("nom");
				String prenom = results.getString("prenom");
				String email = results.getString("email");
				String ville = results.getString("ville");
				String telephone = results.getString("telephone");
				String description = results.getString("description");
				Boolean  disponibilite = results.getBoolean("disponibilite");
				double tarif = results.getDouble("tarif");
				

				prestatireList.add(new Prestataire(id, metier, nom, prenom, email, ville, telephone, description,
					    disponibilite ? "Disponible" : "Indisponible", tarif));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		prestataireTable.setItems(prestatireList);
	}

	private void loadAllPrestataires() {

		prestatireList.clear();
		getAllPrestataire();

	}

	@FXML
	void ajouterPrestataire(ActionEvent event) {
		try {
			// Load the FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("addPrestataireForm.fxml"));
			HBox root = loader.load();

			// Get the current stage (window) and set the new scene
			Stage stage = (Stage) ajouterButtonID.getScene().getWindow();
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @FXML
    void viewClientsButton(ActionEvent event) {
    	
    	DBUtils.changeScene( event, "clientsTable.fxml");

    }
    
    @FXML
    void viewPrestatairesButton(ActionEvent event) {
  
    	DBUtils.changeScene( event, "prestataireTable.fxml");

    }
    
    @FXML
    void viewReservationsButton(ActionEvent event) {
  
    	DBUtils.changeScene( event, "reservationTable.fxml");

    }

    @FXML
    void logOut(ActionEvent event) {
    	
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Se Deconnecter");
		confirmationAlert.setHeaderText(null);
		confirmationAlert.setContentText("Êtes-vous sûr de se deconnecter ?");

		// Wait for the user to respond
		Optional<ButtonType> result = confirmationAlert.showAndWait();
        //if he chosed ok
		if (result.isPresent() && result.get() == ButtonType.OK) {
			

			try {
				// Load the FXML file
				FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
				HBox root = loader.load();

				// Get the current stage (window) and set the new scene
				Stage stage = (Stage) logOutButtonID.getScene().getWindow();
				stage.setScene(new Scene(root));
				stage.centerOnScreen();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} 
		//if he chosed cancel clear the selection in table
		else {
			// User cancelled, clear the selection
			prestataireTable.getSelectionModel().clearSelection();
		}

    }

    @FXML
	void supprimerPrestataire(ActionEvent event) {
		// Récupérer le prestataire sélectionné dans la table
		Prestataire selectedPrestataire = prestataireTable.getSelectionModel().getSelectedItem();

		// Vérifier si un prestataire est sélectionné
		if (selectedPrestataire != null) {
			// Afficher une boîte de dialogue de confirmation
			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation de suppression");
			confirmationAlert.setHeaderText(null);
			confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce prestataire ?");

			// Attendre la réponse de l'utilisateur
			Optional<ButtonType> result = confirmationAlert.showAndWait();

			// Si l'utilisateur confirme (OK)
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Supprimer le prestataire de la base de données
				deletePrestataireFromDatabase(selectedPrestataire);

				// Supprimer le prestataire de la liste observable liée à la table
				prestatireList.remove(selectedPrestataire);

				// Désélectionner l'élément
				prestataireTable.getSelectionModel().clearSelection();

				// Afficher un message de confirmation
				Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
				successAlert.setTitle("Succès");
				successAlert.setHeaderText(null);
				successAlert.setContentText("Le prestataire a été supprimé avec succès.");
				successAlert.showAndWait();
			}
			// Si l'utilisateur annule
			else {
				// Annulation, désélectionner dans la table
				prestataireTable.getSelectionModel().clearSelection();
			}
		}
		// Si aucun prestataire n'est sélectionné
		else {
			// Afficher une alerte si aucun prestataire n'est sélectionné
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun prestataire sélectionné");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez sélectionner un prestataire à supprimer.");
			warningAlert.showAndWait();
		}
	}

	private void deletePrestataireFromDatabase(Prestataire prestataire) {
		Connection connection = MysqlConnection.getDBConnection();
		String sql = "DELETE FROM prestataire WHERE prestataire_id = ?";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, prestataire.getId()); // Adapter selon votre classe Prestataire
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			// Afficher une alerte en cas d'erreur
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Erreur");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText("Une erreur est survenue lors de la suppression du prestataire.");
			errorAlert.showAndWait();
		}
	}

	@FXML
	void modifierPrestataire(ActionEvent event) {
		// Récupérer le prestataire sélectionné
		Prestataire selectedPrestataire = prestataireTable.getSelectionModel().getSelectedItem();

		// Si aucun prestataire n'est sélectionné, afficher un avertissement
		if (selectedPrestataire == null) {
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun prestataire sélectionné");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez sélectionner un prestataire à modifier.");
			warningAlert.showAndWait();
			return;
		}

		try {
			// Charger la fenêtre de modification
			FXMLLoader loader = new FXMLLoader(getClass().getResource("modifierPrestataireForm.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));

			// Passer le prestataire sélectionné à la fenêtre de modification
			ModifierPrestataireController controller = loader.getController();
			controller.setSelectedPrestataire(selectedPrestataire); // Pass the selected prestataire

			// Afficher la fenêtre de modification
			stage.showAndWait();

			// Actualiser le tableau après modification
			prestataireTable.refresh();

			// Si nécessaire, actualiser d'autres informations liées aux prestataires
			getAllPrestataire();

		} catch (IOException e) {
			e.printStackTrace();
			// Show an alert if loading FXML fails
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Erreur de chargement");
			errorAlert.setHeaderText("Erreur lors du chargement de la fenêtre de modification.");
			errorAlert.setContentText("Une erreur est survenue. Veuillez réessayer.");
			errorAlert.showAndWait();
		}
	}
	@FXML
	void exporter(ActionEvent event) {
	    try {
	        // Préparer les données à envoyer
	        StringBuilder postData = new StringBuilder();
	        for (Prestataire prestataire : prestatireList) {
	            postData.append(prestataire.getNom()).append(",");
	            postData.append(prestataire.getPrenom()).append(",");
	            postData.append(prestataire.getEmail()).append(",");
	            postData.append(prestataire.getTelephone()).append(",");
	            postData.append(prestataire.getVille()).append(",");
	            postData.append(prestataire.getMetier()).append(",");
	            postData.append(prestataire.getDescription()).append(",");
	            postData.append(prestataire.getDisponibilite()).append(",");
	            postData.append(prestataire.getTarif()).append("\n");
	        }

	        // Sélectionner où sauvegarder le fichier via FileChooser
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Enregistrer le fichier Excel");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
	        java.io.File file = fileChooser.showSaveDialog(null);

	        if (file != null) {
	            // Envoyer les données au script PHP
	        	 URL url = new URL("http://localhost/javafx_export/export_prestataires.php");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	            // Envoyer les données
	            try (OutputStream os = conn.getOutputStream()) {
	                os.write(postData.toString().getBytes("UTF-8"));
	            }

	            // Lire la réponse et sauvegarder le fichier Excel
	            try (InputStream in = conn.getInputStream();
	                 FileOutputStream out = new FileOutputStream(file)) {
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                System.out.println("Fichier Excel exporté avec succès : " + file.getAbsolutePath());
	            }
	        } else {
	            System.out.println("L'utilisateur a annulé l'enregistrement.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Erreur lors de l'exportation : " + e.getMessage());
	    }
	}


}
