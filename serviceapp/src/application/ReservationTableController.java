package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReservationTableController {
	@FXML
	private TableView<Reservation> reservationTable;

	@FXML
	private Button ajouterButtonID;
	@FXML
	private Button modifierButtonID2;

	
	
	@FXML
	private Button supprimerButtonID;
	@FXML
	private TextField searchTextFieldID;

	@FXML
	private TableColumn<Reservation, String> dateDebutColumn;

	@FXML
	private TableColumn<Reservation, String> dateFinColumn;

	@FXML
	private TableColumn<Reservation, String> nomCompleteClientColumn;

	@FXML
	private TableColumn<Reservation, String> nomCompletePrestataireColumn;

	@FXML
	private Button statutButtonID;

	@FXML
	private TableColumn<Reservation, String> statutColumn;

	@FXML
	private TableColumn<Reservation, String> telephoneClientColumn;

	@FXML
	private TableColumn<Reservation, String> telephonePrestataireColumn;

	@FXML
	void ajouterReservation(ActionEvent event) {
		try {
			// Load the FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("addReservationForm.fxml"));
			HBox root = loader.load();

			// Get the current stage (window) and set the new scene
			Stage stage = (Stage) ajouterButtonID.getScene().getWindow();
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private ObservableList<Reservation> reservtationList = FXCollections.observableArrayList();

	public void initialize() {
		nomCompleteClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomCompletClient"));
		telephoneClientColumn.setCellValueFactory(new PropertyValueFactory<>("telephoneClient"));

		nomCompletePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("nomCompletPrestataire"));
		telephonePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("telephonePrestataire"));

		dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
		dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
		statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

		getAllReservations();
		searchTextFieldID.textProperty().addListener((observable, oldValue, newValue) -> {
			String searchQuery = newValue.toLowerCase().trim();
			if (searchQuery.isEmpty()) {
				reservationTable.setItems(reservtationList);
			} else {
				filterReservations(searchQuery);
			}
		});

	}

	private void filterReservations(String searchQuery) {
		ObservableList<Reservation> filteredList = FXCollections.observableArrayList();

		for (Reservation reservation : reservtationList) {
			if (reservation.getNomCompletClient().toLowerCase().contains(searchQuery)
					|| reservation.getNomCompletPrestataire().toLowerCase().contains(searchQuery)
					|| reservation.getStatut().toLowerCase().contains(searchQuery)
					|| reservation.getDateDebut().contains(searchQuery)
					|| reservation.getDateFin().contains(searchQuery)) {
				filteredList.add(reservation);
			}
		}
		reservationTable.setItems(filteredList);
	}

	// get all the clients from the database
//Get all the clients from the database
	public void getAllReservations() {
		reservtationList.clear();

		Connection connection = MysqlConnection.getDBConnection();
		String sql = "SELECT c.client_id, CONCAT(c.nom, ' ', c.prenom) AS nomCompletClient, "
				+ "c.telephone AS telephoneClient, " + // Correction ici
				"p.prestataire_id,CONCAT(p.nom, ' ', p.prenom) AS nomCompletPrestataire, "
				+ "p.telephone AS telephonePrestataire, " + "r.reservation_id, " + "r.date_debut as dateDebut, "
				+ "r.date_fin as dateFin, " + "s.statut " + "FROM client c "
				+ "JOIN reservation r ON c.client_id = r.client_id "
				+ "JOIN prestataire p ON p.prestataire_id = r.prestataire_id "
				+ "JOIN statut s ON s.statut_id = r.statut_id;";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet results = ps.executeQuery();

			while (results.next()) {
				int idClient = results.getInt("client_id");
				String nomCompletClient = results.getString("nomCompletClient");

				String telephoneClient = results.getString("telephoneClient");

				int idPrestataire = results.getInt("prestataire_id");
				String nomCompletPrestataire = results.getString("nomCompletPrestataire");
				String telephonePrestataire = results.getString("telephonePrestataire");

				int idReservtion = results.getInt("reservation_id");
				String dateDebut = results.getString("dateDebut");
				String dateFin = results.getString("dateFin");
				String statut = results.getString("statut");

				reservtationList.add(new Reservation(idClient, nomCompletClient, telephoneClient, idPrestataire,
						nomCompletPrestataire, telephonePrestataire, idReservtion, statut, dateDebut, dateFin));

			}

			// Appliquer la liste au TableView après avoir ajouté tous les éléments
			reservationTable.setItems(reservtationList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void changeStatut(ActionEvent event) {
		// Get the selected reservation from the table
		Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
		if (selectedReservation == null) {
			showWarningAlert("Changement de statut impossible", "Aucune réservation sélectionnée !");
			return;
		}

		// Create a new stage for the status change dialog
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL); // Modal: blocks the main window
		dialog.setTitle("Changer le statut");

		// Create ComboBox for selecting a status
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll("Terminer", "Annuler", "En cours");
		comboBox.setValue(selectedReservation.getStatut()); // Pre-fill with the current status

		// Buttons for confirming or cancelling the change
		Button okButton = new Button("Confirmer");
		Button cancelButton = new Button("Annuler");

		// OK Button action: update the status in the database
		okButton.setOnAction(e -> {
			String selectedStatut = comboBox.getValue();

			// Update the reservation object with the new status
			selectedReservation.setStatut(selectedStatut);

			// Retrieve the statut_id based on the selected status
			String selectStatutIdQuery = "SELECT statut_id FROM statut WHERE statut = ?";
			try (Connection connection = MysqlConnection.getDBConnection();
					PreparedStatement psSelect = connection.prepareStatement(selectStatutIdQuery)) {

				psSelect.setString(1, selectedStatut);
				ResultSet rs = psSelect.executeQuery();

				if (rs.next()) {
					int statutId = rs.getInt("statut_id");

					// Now update the reservation with the statut_id in the database
					String updateQuery = "UPDATE reservation SET statut_id = ? WHERE reservation_id = ?";
					try (PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {
						psUpdate.setInt(1, statutId);
						psUpdate.setInt(2, selectedReservation.getIdReservtion());

						int rowsAffected = psUpdate.executeUpdate();
						if (rowsAffected > 0) {
							showInfoAlert("Changement de statut réussi", "Le statut a été modifié avec succès.");

							// Now update the status in the list
							selectedReservation.setStatut(selectedStatut); // Update the status in the list

							// Refresh the TableView to reflect the change
							reservationTable.refresh(); // <--- This should be here to refresh the TableView
						} else {
							showErrorAlert("Échec", "Impossible de modifier le statut de la réservation.", null);
						}
					}

				} else {
					showErrorAlert("Statut introuvable",
							"Le statut sélectionné est introuvable dans la base de données.", selectStatutIdQuery);
				}

			} catch (Exception ex) {
				showErrorAlert("Erreur", "Une erreur s'est produite lors du changement de statut.", ex.getMessage());
			}

			// Close the dialog after the update
			dialog.close();
		});

		// Cancel Button action: simply close the dialog
		cancelButton.setOnAction(e -> dialog.close());

		// Layout for the dialog
		HBox dialogVBox = new HBox(10, new Label("Sélectionnez un statut :"), comboBox, okButton, cancelButton);
		dialogVBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

		// Set the dialog scene
		Scene dialogScene = new Scene(dialogVBox, 600, 200);
		dialog.setScene(dialogScene);
		dialog.show(); // Show the dialog
	}

	@FXML
	void supprimerReservation(ActionEvent event) {
		Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
		if (selectedReservation == null) {
			showWarningAlert("Suppression impossible", "Aucune réservation sélectionnée !");
			return;
		}

		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirmation de suppression");
		confirmationAlert.setHeaderText("Supprimer la réservation ?");
		confirmationAlert.setContentText("Voulez-vous vraiment supprimer cette réservation ?");
		confirmationAlert.getButtonTypes().setAll(new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE),
				new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE));

		Optional<ButtonType> result = confirmationAlert.showAndWait();
		if (result.isEmpty() || result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
			return;
		}

		String deleteQuery = "DELETE FROM reservation WHERE reservation_id = ?";
		try (Connection connection = MysqlConnection.getDBConnection();
				PreparedStatement ps = connection.prepareStatement(deleteQuery)) {

			ps.setInt(1, selectedReservation.getIdReservtion());
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				// Remove the selected reservation from the observable list
				reservtationList.remove(selectedReservation); // Use the correct list (reservtationList)
				showInfoAlert("Suppression réussie", "La réservation a été supprimée avec succès.");
			} else {
				showErrorAlert("Échec", "Impossible de supprimer la réservation.", null);
			}
		} catch (Exception e) {
			showErrorAlert("Erreur", "Une erreur s'est produite lors de la suppression.", e.getMessage());
		}
	}

	private void showErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void showWarningAlert(String header, String content) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Avertissement");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void showInfoAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@FXML
	void modifierReservation(ActionEvent event) {
		// Récupérer la réservation sélectionnée
		Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
		
		

		if (selectedReservation == null) {
			// Afficher une alerte si aucune réservation n'est sélectionnée
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucune réservation sélectionnée");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez sélectionner une réservation à modifier.");
			warningAlert.showAndWait();
			return;
		}
		
		System.out.println(selectedReservation.getIdReservtion()); //error existe here 
		System.out.println(selectedReservation.getDateDebut());

		try {
			// Charger le formulaire de modification
			FXMLLoader loader = new FXMLLoader(getClass().getResource("modifierReservationForm.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));

			// Passer la réservation sélectionnée au contrôleur de la fenêtre de
			// modification
			modifierReservationController controller = loader.getController();
			controller.setSelectedReservation(selectedReservation);

			// Afficher la fenêtre de modification
			stage.initModality(Modality.APPLICATION_MODAL); // Bloquer la fenêtre principale
			stage.setTitle("Modifier Réservation");
			stage.showAndWait();

			// Actualiser la table après la modification
			reservationTable.refresh();

			// Recharger les données depuis la base si nécessaire
			getAllReservations();

		} catch (IOException e) {
			e.printStackTrace();
			showErrorAlert("Erreur", "Une erreur s'est produite lors de l'ouverture de la fenêtre de modification.",
					e.getMessage());
		}
	}

}