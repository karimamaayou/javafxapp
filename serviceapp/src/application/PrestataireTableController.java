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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
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
    private Button refrechButtonID;
	

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
	private Button btn_recherche;

	private ObservableList<Prestataire> prestatireList = FXCollections.observableArrayList();
	
	@FXML
	private TableColumn<Prestataire, String> metierColumn;

	@FXML
	private TextField tf_recherche;
	@FXML
    private Pagination paginationID;
	@FXML
    private ChoiceBox<Integer> pageSizeChoiceBox;
	
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
		
		
		   paginationID.setPageCount((int) Math.ceil(prestatireList.size() / (double) ITEMS_PER_PAGE));
		    setupPagination();

		    // Charger la premi�re page
		    loadPage(0);

		    // Configurer la recherche
		    setupSearchFunctionality();
		    

	        // Configurer le choix de la taille des pages
	        setupPageSizeChoiceBox();

	        // Configurer la pagination
	        setupPagination();

	        // Mettre � jour la pagination pour initialiser l'affichage
	        updatePagination();
	}
	
    @FXML
    void refrechDisponiblite(ActionEvent event) {
        // Requ�te SQL pour mettre � jour la disponibilit� des prestataires en fonction des r�servations
        String updateSQL = "UPDATE prestataire p " +
                "JOIN (SELECT p.prestataire_id, COUNT(r.reservation_id) as nbr " +
                "       FROM prestataire p " +
                "       LEFT JOIN reservation r USING(prestataire_id) " +
                "       GROUP BY p.prestataire_id) AS reservations " +
                "ON p.prestataire_id = reservations.prestataire_id " +
                "SET p.disponibilite = CASE " +
                "WHEN reservations.nbr > 0 THEN 0 " +
                "ELSE 1 END;";

        // Connexion � la base de donn�es
        try (Connection connection = MysqlConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(updateSQL)) {
            
            // Ex�cuter la requ�te de mise � jour
            int rowsAffected = ps.executeUpdate();
            
            // V�rifier si des lignes ont �t� affect�es
            if (rowsAffected > 0) {
                // Si des prestataires ont �t� mis � jour, rafra�chir les donn�es de la table
                loadAllPrestataires();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Vous pouvez loguer l'erreur ou g�rer l'exception de mani�re appropri�e sans alerter l'utilisateur
        }

    }

	public void getAllPrestataire() {
		prestatireList.clear();

		// Connexion � la base de donn�es
		Connection connection = MysqlConnection.getDBConnection();

		// Requ�te SQL corrig�e pour �viter les ambigu�t�s et r�cup�rer les noms des
		// villes et des m�tiers
		String sql = "SELECT p.prestataire_id, m.metier, p.nom, p.prenom, p.email, v.ville, p.telephone, p.description, p.disponibilite, p.tarif "
				+ "FROM prestataire p " + "JOIN ville v ON p.ville_id = v.ville_id "
				+ "JOIN metier m ON p.metier_id = m.metier_id";

		try {
			// Pr�parer et ex�cuter la requ�te
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet results = ps.executeQuery();

			// Parcourir les r�sultats de la requ�te    
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
			

				// Ajouter l'objet Prestataire � la liste
				prestatireList.add(new Prestataire(id, metier, nom, prenom, email, ville, telephone, description,
						disponibilite  ? "Disponible":"Indisponible",tarif
					));
			}

			// Mettre � jour la TableView avec la liste des prestataires
			prestataireTable.setItems(prestatireList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// la rechaire des valeur pour le table de prestataire
	@FXML
	private void handleSearch(ActionEvent event) {
		String searchText = tf_recherche.getText(); // R�cup�rer la valeur entr�e par l'utilisateur

		if (!searchText.isEmpty()) {
			// Effectuer la recherche dans la base de donn�es
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
		DBUtils.changeScene( event, "addPrestataireForm.fxml");
	}


    @FXML
	void supprimerPrestataire(ActionEvent event) {
		// R�cup�rer le prestataire s�lectionn� dans la table
		Prestataire selectedPrestataire = prestataireTable.getSelectionModel().getSelectedItem();

		// V�rifier si un prestataire est s�lectionn�
		if (selectedPrestataire != null) {
			// Afficher une bo�te de dialogue de confirmation
			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation de suppression");
			confirmationAlert.setHeaderText(null);
			confirmationAlert.setContentText("�tes-vous s�r de vouloir supprimer ce prestataire ?");

			// Attendre la r�ponse de l'utilisateur
			Optional<ButtonType> result = confirmationAlert.showAndWait();

			// Si l'utilisateur confirme (OK)
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Supprimer le prestataire de la base de donn�es
				deletePrestataireFromDatabase(selectedPrestataire);

				// Supprimer le prestataire de la liste observable li�e � la table
				prestatireList.remove(selectedPrestataire);

				// D�s�lectionner l'�l�ment
				prestataireTable.getSelectionModel().clearSelection();
				loadAllPrestataires();
				// Afficher un message de confirmation
				Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
				successAlert.setTitle("Succ�s");
				successAlert.setHeaderText(null);
				successAlert.setContentText("Le prestataire a �t� supprim� avec succ�s.");
				successAlert.showAndWait();
				
			}
			// Si l'utilisateur annule
			else {
				// Annulation, d�s�lectionner dans la table
				prestataireTable.getSelectionModel().clearSelection();
			}
		}
		// Si aucun prestataire n'est s�lectionn�
		else {
			// Afficher une alerte si aucun prestataire n'est s�lectionn�
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun prestataire s�lectionn�");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez s�lectionner un prestataire � supprimer.");
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
		// R�cup�rer le prestataire s�lectionn�
		Prestataire selectedPrestataire = prestataireTable.getSelectionModel().getSelectedItem();

		// Si aucun prestataire n'est s�lectionn�, afficher un avertissement
		if (selectedPrestataire == null) {
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun prestataire s�lectionn�");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez s�lectionner un prestataire � modifier.");
			warningAlert.showAndWait();
			return;
		}

		try {
			// Charger la fen�tre de modification
			FXMLLoader loader = new FXMLLoader(getClass().getResource("modifierPrestataireForm.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(loader.load()));

			// Passer le prestataire s�lectionn� � la fen�tre de modification
			ModifierPrestataireController controller = loader.getController();
			controller.setSelectedPrestataire(selectedPrestataire); // Pass the selected prestataire

			// Afficher la fen�tre de modification
			stage.showAndWait();

			// Actualiser le tableau apr�s modification
			prestataireTable.refresh();

			// Si n�cessaire, actualiser d'autres informations li�es aux prestataires
			getAllPrestataire();

		} catch (IOException e) {
			e.printStackTrace();
			// Show an alert if loading FXML fails
			Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			errorAlert.setTitle("Erreur de chargement");
			errorAlert.setHeaderText("Erreur lors du chargement de la fen�tre de modification.");
			errorAlert.setContentText("Une erreur est survenue. Veuillez r�essayer.");
			errorAlert.showAndWait();
		}
	}
	@FXML
	void exporter(ActionEvent event) {
	    try {
	        // Pr�parer les donn�es � envoyer
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

	        // S�lectionner o� sauvegarder le fichier via FileChooser
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Enregistrer le fichier Excel");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
	        java.io.File file = fileChooser.showSaveDialog(null);

	        if (file != null) {
	            // Envoyer les donn�es au script PHP
	        	 URL url = new URL("http://localhost/javafx_export/export_prestataires.php");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	            // Envoyer les donn�es
	            try (OutputStream os = conn.getOutputStream()) {
	                os.write(postData.toString().getBytes("UTF-8"));
	            }

	            // Lire la r�ponse et sauvegarder le fichier Excel
	            try (InputStream in = conn.getInputStream();
	                 FileOutputStream out = new FileOutputStream(file)) {
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                System.out.println("Fichier Excel export� avec succ�s : " + file.getAbsolutePath());
	            }
	        } else {
	            System.out.println("L'utilisateur a annul� l'enregistrement.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Erreur lors de l'exportation : " + e.getMessage());
	    }
	}
	//prestatireList
	private static final int ITEMS_PER_PAGE = 10;
	 // Assurez-vous que cette variable est bien initialis�e.
	

	// Charger une page sp�cifique
	private void loadPage(int pageIndex) {
	    int fromIndex = pageIndex * ITEMS_PER_PAGE;
	    int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, prestatireList.size());

	    if (fromIndex > prestatireList.size()) {
	        return; // Pas de donn�es � charger pour cet index
	    }

	    ObservableList<Prestataire> pageData = FXCollections.observableArrayList(prestatireList.subList(fromIndex, toIndex));

	    // Mettre � jour la TableView avec les donn�es de la page actuelle
	    prestataireTable.setItems(pageData);
	}

	// Filtrer les prestataires en fonction de la recherche
	private void filterClients(String searchQuery) {
	    ObservableList<Prestataire> filteredList = FXCollections.observableArrayList();

	    for (Prestataire prestataire : prestatireList) {
	        if (prestataire.getNom().toLowerCase().contains(searchQuery) || 
	            prestataire.getPrenom().toLowerCase().contains(searchQuery) || 
	            prestataire.getEmail().toLowerCase().contains(searchQuery) || 
	            prestataire.getTelephone().toLowerCase().contains(searchQuery)) {
	            filteredList.add(prestataire);
	        }
	    }

	    // Mettre � jour le nombre de pages pour la pagination
	    paginationID.setPageCount((int) Math.ceil(filteredList.size() / (double) ITEMS_PER_PAGE));

	    // Charger la premi�re page des r�sultats filtr�s
	    loadFilteredPage(0, filteredList);
	}

	// Charger une page sp�cifique des r�sultats filtr�s
	private void loadFilteredPage(int pageIndex, ObservableList<Prestataire> filteredList) {
	    int fromIndex = pageIndex * ITEMS_PER_PAGE;
	    int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredList.size());

	    if (fromIndex > filteredList.size()) {
	        return; // Pas de donn�es � charger pour cet index
	    }

	    ObservableList<Prestataire> pageData = FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));

	    // Mettre � jour la TableView avec les donn�es filtr�es pour la page actuelle
	    prestataireTable.setItems(pageData);
	}

	// Configuration de la pagination
	private void setupPagination() {
	    paginationID.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
	        loadPage(newValue.intValue());
	    });
	    paginationID.setCurrentPageIndex(0); // D�marrer � la premi�re page

	    // Ajouter un Listener pour �couter les changements de page
	    paginationID.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> updatePagination());
	}

	// Configuration de la fonctionnalit� de recherche
	private void setupSearchFunctionality() {
	    TextField tf_recherche = new TextField(); // Assurez-vous que ce champ de texte est bien initialis� dans votre interface.
	    tf_recherche.textProperty().addListener((observable, oldValue, newValue) -> {
	        String searchQuery = newValue.toLowerCase().trim();
	        if (searchQuery.isEmpty()) {
	            paginationID.setPageCount((int) Math.ceil(prestatireList.size() / (double) ITEMS_PER_PAGE));
	            loadPage(0); // Recharger la premi�re page des donn�es non filtr�es
	        } else {
	            filterClients(searchQuery);
	        }
	    });
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	

	// M�thode pour configurer la s�lection de la taille des pages
	private void setupPageSizeChoiceBox() {
	    // Ajouter les options de tailles de page
		pageSizeChoiceBox.setItems(FXCollections.observableArrayList(10, 20, 50, 100));
	    pageSizeChoiceBox.setValue(DEFAULT_PAGE_SIZE); // Valeur par d�faut

	    // Ajouter un �v�nement pour mettre � jour la pagination lorsqu'une nouvelle taille est s�lectionn�e
	    pageSizeChoiceBox.setOnAction(event -> {
	        paginationID.setCurrentPageIndex(0); // R�initialiser � la premi�re page
	        updatePagination(); // Mettre � jour la pagination
	    });
	}

	

	// M�thode pour mettre � jour la pagination
	private void updatePagination() {
	    // Obtenir la taille actuelle des pages s�lectionn�e par l'utilisateur
	    int pageSize = pageSizeChoiceBox.getValue();

	    // Calculer le nombre total de pages
	    int pageCount = (int) Math.ceil(prestatireList.size() / (double) pageSize);
	    paginationID.setPageCount(pageCount);

	    // Charger les donn�es pour la page actuelle
	    loadPage(paginationID.getCurrentPageIndex(), pageSize);
	}

	// M�thode pour charger une page sp�cifique en fonction de la taille des pages
	private void loadPage(int pageIndex, int pageSize) {
	    int fromIndex = pageIndex * pageSize;
	    int toIndex = Math.min(fromIndex + pageSize, prestatireList.size());

	    if (fromIndex >= prestatireList.size()) {
	        return; // Si aucun �l�ment � afficher, arr�ter
	    }

	    // Cr�er une sous-liste des �l�ments pour cette page
	    ObservableList<Prestataire> pageData = FXCollections.observableArrayList(prestatireList.subList(fromIndex, toIndex));

	    // Mettre � jour la TableView avec les donn�es de la page actuelle
	    prestataireTable.setItems(pageData);
	}


	}	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
