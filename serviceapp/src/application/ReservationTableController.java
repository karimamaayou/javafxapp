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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReservationTableController {
	@FXML
	private TableColumn<Reservation, String> metierColumn;

	@FXML
	private ChoiceBox<Integer> pageSizeChoiceBox; // For pagination

	@FXML
	private Pagination paginationID;

	@FXML
	private TableView<Reservation> reservationTable;

	@FXML
	private TableColumn<Reservation, String> nomCompleteClientColumn;

	@FXML
	private TableColumn<Reservation, String> telephoneClientColumn;

	@FXML
	private TableColumn<Reservation, String> nomCompletePrestataireColumn;

	@FXML
	private TableColumn<Reservation, String> telephonePrestataireColumn;

	@FXML
	private TableColumn<Reservation, String> dateDebutColumn;

	@FXML
	private TableColumn<Reservation, String> dateFinColumn;

	@FXML
	private TableColumn<Reservation, String> statutColumn;

	@FXML
	private TableColumn<Reservation, Double> prix_Column;

	@FXML
	private TextField searchTextFieldID;

	@FXML
	private Button ajouterButtonID, modifierButtonID2, supprimerButtonID, statutButtonID, exporterButtonID;

	private ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
	private ObservableList<Reservation> currentPageData = FXCollections.observableArrayList();
	private static final int DEFAULT_PAGE_SIZE = 10;

	public void initialize() {
		setupTableColumns();
		setupSearchFunctionality();
		setupPageSizeChoiceBox();
		setupPagination();
		getAllReservations();
	}

	private void setupTableColumns() {
		nomCompleteClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomCompletClient"));
		telephoneClientColumn.setCellValueFactory(new PropertyValueFactory<>("telephoneClient"));
		nomCompletePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("nomCompletPrestataire"));
		telephonePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("telephonePrestataire"));
		dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
		dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
		statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
		prix_Column.setCellValueFactory(new PropertyValueFactory<>("prix"));
		metierColumn.setCellValueFactory(new PropertyValueFactory<>("metier"));
	}

	private void setupSearchFunctionality() {
		searchTextFieldID.textProperty().addListener((observable, oldValue, newValue) -> {
			String searchQuery = newValue.toLowerCase().trim();
			if (searchQuery.isEmpty()) {
				reservationTable.setItems(currentPageData);
			} else {
				filterReservations(searchQuery);
			}
		});
	}

	private void filterReservations(String searchQuery) {
		ObservableList<Reservation> filteredList = FXCollections.observableArrayList();
		for (Reservation reservation : reservationList) {
			if (reservation.getNomCompletClient().toLowerCase().contains(searchQuery)
					|| reservation.getNomCompletPrestataire().toLowerCase().contains(searchQuery)
					|| reservation.getStatut().toLowerCase().contains(searchQuery)) {
				filteredList.add(reservation);
			}
		}

		// Mise à jour du nombre de pages pour la liste filtrée
		paginationID.setPageCount((int) Math.ceil(filteredList.size() / (double) pageSizeChoiceBox.getValue()));

		// Chargement de la première page de la liste filtrée
		loadFilteredPage(0, filteredList);

		// Écouteur pour la pagination après le filtrage
		paginationID.currentPageIndexProperty()
				.addListener((observable, oldValue, newValue) -> loadFilteredPage(newValue.intValue(), filteredList));
	}

	private void loadFilteredPage(int pageIndex, ObservableList<Reservation> filteredList) {
		int pageSize = pageSizeChoiceBox.getValue(); // Taille de la page sélectionnée
		int fromIndex = pageIndex * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, filteredList.size());
		ObservableList<Reservation> pageData = FXCollections
				.observableArrayList(filteredList.subList(fromIndex, toIndex));

		// Mise à jour de la TableView avec les données filtrées pour la page actuelle
		reservationTable.setItems(pageData);
	}

	private void setupPageSizeChoiceBox() {
		pageSizeChoiceBox.setItems(FXCollections.observableArrayList(10, 20, 50, 100));
		pageSizeChoiceBox.setValue(DEFAULT_PAGE_SIZE);
		pageSizeChoiceBox.setOnAction(event -> {
			paginationID.setCurrentPageIndex(0);
			updatePagination();
		});
	}

	private void setupPagination() {
		paginationID.setCurrentPageIndex(0);
		paginationID.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> updatePagination());
	}

	private void updatePagination() {
		int pageSize = pageSizeChoiceBox.getValue();
		int currentPage = paginationID.getCurrentPageIndex();
		int totalItems = reservationList.size();
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);

		paginationID.setPageCount(totalPages);

		int fromIndex = currentPage * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, totalItems);

		currentPageData.setAll(reservationList.subList(fromIndex, toIndex));
		reservationTable.setItems(currentPageData);
	}

	public void getAllReservations() {
		reservationList.clear();
		Connection connection = MysqlConnection.getDBConnection();
		String sql = " SELECT c.client_id, CONCAT(c.nom, ' ', c.prenom) AS nomCompletClient, \r\n"
				+ "       c.telephone AS telephoneClient, p.prestataire_id, \r\n"
				+ "       CONCAT(p.nom, ' ', p.prenom) AS nomCompletPrestataire, \r\n"
				+ "       m.metier AS metier, p.telephone AS telephonePrestataire, \r\n"
				+ "       r.reservation_id, r.date_debut AS dateDebut, \r\n"
				+ "       r.date_fin AS dateFin, s.statut, r.prix\r\n"
				+ "FROM client c\r\n"
				+ "JOIN reservation r ON c.client_id = r.client_id\r\n"
				+ "JOIN prestataire p ON p.prestataire_id = r.prestataire_id\r\n"
				+ "JOIN metier m ON p.metier_id = m.metier_id\r\n"
				+ "JOIN statut s ON s.statut_id = r.statut_id;  -- Fixed the space issue\r\n"
				+ "";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet results = ps.executeQuery();
			while (results.next()) {
				reservationList.add(new Reservation(results.getInt("client_id"), results.getString("nomCompletClient"),
						results.getString("telephoneClient"), results.getInt("prestataire_id"),
						results.getString("nomCompletPrestataire"), results.getString("telephonePrestataire"),
						results.getInt("reservation_id"), results.getString("statut"), results.getString("dateDebut"),
						results.getString("dateFin"), results.getDouble("prix"),results.getString("metier") ));
			}
			updatePagination();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void ajouterReservation(ActionEvent event) {
		DBUtils.changeScene(event, "addReservationForm.fxml");
	}

	@FXML
	void modifierReservation(ActionEvent event) {
		Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
		if (selectedReservation == null) {
			showAlert(Alert.AlertType.WARNING, "Modification impossible", "Aucune réservation sélectionnée !");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("modifierReservationForm.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));
			modifierReservationController controller = loader.getController();
			controller.setSelectedReservation(selectedReservation);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			getAllReservations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void supprimerReservation(ActionEvent event) {
		Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
		if (selectedReservation == null) {
			showAlert(Alert.AlertType.WARNING, "Suppression impossible", "Aucune réservation sélectionnée !");
			return;
		}
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette réservation ?", ButtonType.YES,
				ButtonType.NO);
		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			String query = "DELETE FROM reservation WHERE reservation_id = ?";
			try (Connection connection = MysqlConnection.getDBConnection();
					PreparedStatement ps = connection.prepareStatement(query)) {
				ps.setInt(1, selectedReservation.getIdReservtion());
				ps.executeUpdate();
				// Remove from reservationList
				reservationList.remove(selectedReservation);
				// Refresh the table view to reflect the change
				reservationTable.setItems(reservationList);
				showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
						"La réservation a été supprimée avec succès.");
			} catch (Exception e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
						"Une erreur est survenue lors de la suppression.");
			}
		}
	}

	@FXML
	void changeStatut(ActionEvent event) {
	    // Get the selected reservation from the TableView
	    Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

	    // Check if a reservation is selected
	    if (selectedReservation == null) {
	        showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réservation à mettre à jour.");
	        return;
	    }

	    // Display a dialog to choose the new status
	    ChoiceDialog<String> dialog = new ChoiceDialog<>("Terminer", "Annuler");
	    dialog.setTitle("Changer le statut");
	    dialog.setHeaderText("Sélectionnez le nouveau statut pour cette réservation");
	    dialog.setContentText("Statut :");
	    Optional<String> result = dialog.showAndWait();

	    if (result.isEmpty()) {
	        showAlert(Alert.AlertType.INFORMATION, "Action annulée", "Aucun changement n'a été effectué.");
	        return;
	    }

	    String newStatut = result.get(); // New status chosen
	    Connection connection = null;
	    PreparedStatement psInsertHistorique = null;
	    PreparedStatement psDeleteReservation = null;

	    try {
	        connection = MysqlConnection.getDBConnection();
	        connection.setAutoCommit(false); // Start a transaction

	        // Insert into the history table
	        String insertHistoriqueQuery = "INSERT INTO historique (nomClient, tele_Client, nomPrestataire, tele_Prestataire, date_debut, date_fin, date_archive, metier, prix, statut) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), ?, ?, ?)";
	        psInsertHistorique = connection.prepareStatement(insertHistoriqueQuery);
	        psInsertHistorique.setString(1, selectedReservation.getNomCompletClient());
	        psInsertHistorique.setString(2, selectedReservation.getTelephoneClient());
	        psInsertHistorique.setString(3, selectedReservation.getNomCompletPrestataire());
	        psInsertHistorique.setString(4, selectedReservation.getTelephonePrestataire());
	        psInsertHistorique.setString(5, selectedReservation.getDateDebut());
	        psInsertHistorique.setString(6, selectedReservation.getDateFin());
	        psInsertHistorique.setString(7, selectedReservation.getMetier());
	        psInsertHistorique.setDouble(8, selectedReservation.getPrix());
	        psInsertHistorique.setString(9, newStatut); // Save the new status
	        psInsertHistorique.executeUpdate();

	        // Delete the reservation from the `reservation` table
	        String deleteReservationQuery = "DELETE FROM reservation WHERE reservation_id = ?";
	        psDeleteReservation = connection.prepareStatement(deleteReservationQuery);
	        psDeleteReservation.setInt(1, selectedReservation.getIdReservtion());
	        psDeleteReservation.executeUpdate();

	        // Remove the reservation from the local list
	        reservationList.remove(selectedReservation);

	        connection.commit(); // Commit transaction

	        // Refresh the table to reflect the changes
	        updatePagination();

	        showAlert(Alert.AlertType.INFORMATION, "Mise à jour réussie", "La réservation a été archivée et supprimée avec succès.");
	    } catch (Exception e) {
	        if (connection != null) {
	            try {
	                connection.rollback(); // Rollback transaction in case of error
	            } catch (SQLException rollbackEx) {
	                rollbackEx.printStackTrace();
	            }
	        }
	        e.printStackTrace();
	        showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour de la réservation.");
	    } finally {
	        try {
	            if (psInsertHistorique != null) psInsertHistorique.close();
	            if (psDeleteReservation != null) psDeleteReservation.close();
	            if (connection != null) connection.close();
	        } catch (SQLException closeEx) {
	            closeEx.printStackTrace();
	        }
	    }
	}







	private void updateReservationStatut(Reservation reservation, String newStatut) {
		String query = "UPDATE reservation SET statut_id = (SELECT statut_id FROM statut WHERE statut = ?) WHERE reservation_id = ?";
		try (Connection connection = MysqlConnection.getDBConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, newStatut);
			ps.setInt(2, reservation.getIdReservtion());
			ps.executeUpdate();
			reservation.setStatut(newStatut);
			reservationTable.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void exporter(ActionEvent event) {
		try {
			// Préparer les données des réservations pour l'envoi au script PHP
			StringBuilder postData = new StringBuilder();
			for (Reservation reservation : reservationList) {
				postData.append(reservation.getNomCompletClient()).append(",");
				postData.append(reservation.getTelephoneClient()).append(",");
				postData.append(reservation.getNomCompletPrestataire()).append(",");
				postData.append(reservation.getTelephonePrestataire()).append(",");
				postData.append(reservation.getDateDebut()).append(",");
				postData.append(reservation.getDateFin()).append(",");
				postData.append(reservation.getStatut()).append(",");
				postData.append(reservation.getPrix()).append("\n");
			}

			// Créer le file chooser
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Exporter les réservations au format Excel");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));

			// Ouvrir le file chooser pour sélectionner le fichier
			java.io.File file = fileChooser.showSaveDialog(null);
			if (file != null) {
				// URL du script PHP
				URL url = new URL("http://localhost/javafx_export/export_reservation.php");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				// Envoyer les données au script PHP
				try (OutputStream os = conn.getOutputStream()) {
					os.write(postData.toString().getBytes("UTF-8"));
				}

				// Gérer la réponse et télécharger le fichier généré
				try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}
					System.out.println("Le fichier Excel a été téléchargé avec succès à l'emplacement : "
							+ file.getAbsolutePath());
					showAlert(Alert.AlertType.INFORMATION, "Exportation réussie",
							"Les réservations ont été exportées avec succès.");
				}
			} else {
				System.out.println("Exportation annulée par l'utilisateur.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erreur lors de l'exportation : " + e.getMessage());
			showAlert(Alert.AlertType.ERROR, "Erreur d'exportation",
					"Une erreur est survenue lors de l'exportation des réservations.");
		}
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.showAndWait();
	}
	

}
