package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HestoriqueTableController {

    @FXML
    private TableView<Historique> hestoriqueTable;

    @FXML
    private TableColumn<Historique, String> dateDebutColumn;

    @FXML
    private TableColumn<Historique, String> dateFinColumn;

    @FXML
    private TableColumn<Historique, String> nomCompleteClientColumn;

    @FXML
    private TableColumn<Historique, String> telephoneClientColumn;

    @FXML
    private TableColumn<Historique, String> nomCompletePrestataireColumn;

    @FXML
    private TableColumn<Historique, String> telephonePrestataireColumn;

    @FXML
    private TableColumn<Historique, String> matierPrestataireColumn;

    @FXML
    private TableColumn<Historique, Double> prix_Column;

    @FXML
    private TableColumn<Historique, String> statutColumn;
    @FXML
    private TableColumn<Historique, String> dateArchiveColumn ;
    @FXML
    private TextField searchTextFieldID;

    @FXML
    private Button exporterHestoriqueButtonID;

    @FXML
    private ChoiceBox<Integer> pageSizeChoiceBox;

    @FXML
    private Pagination paginationID;

    ObservableList<Historique> historiqueList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadHistoriqueData();
        searchTextFieldID.textProperty().addListener((observable, oldValue, newValue) -> searchHistorique(newValue));
        // Configurer la pagination
        setupPagination();

        // Configurer la fonctionnalit� de recherche
        setupSearchFunctionality();

        // Configurer la s�lection de la taille des pages
        setupPageSizeChoiceBox();
    }

    private void setupTableColumns() {
        // Associer les colonnes aux propri�t�s
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        dateArchiveColumn.setCellValueFactory(new PropertyValueFactory<>("dateArchive"));
        nomCompleteClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        telephoneClientColumn.setCellValueFactory(new PropertyValueFactory<>("telephoneClient"));
        nomCompletePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("nomPrestataire"));
        telephonePrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("telephonePrestataire"));
        matierPrestataireColumn.setCellValueFactory(new PropertyValueFactory<>("matierePrestataire"));
        prix_Column.setCellValueFactory(new PropertyValueFactory<>("prix"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    public void loadHistoriqueData() {
       

        // Connexion � la base de donn�es
        Connection connection = MysqlConnection.getDBConnection();

        // Requ�te SQL pour r�cup�rer toutes les donn�es de la table historique
        String sql = "SELECT historique_id, nomClient, tele_Client, nomPrestataire, tele_Prestataire, "
                   + "date_debut, date_fin, metier, prix, date_archive, statut FROM historique";

        try {
            // Pr�parer et ex�cuter la requ�te
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet results = ps.executeQuery();

            // Parcourir les r�sultats de la requ�te
            while (results.next()) {
                int id = results.getInt("historique_id");
                String nomClient = results.getString("nomClient");
                String teleClient = results.getString("tele_Client");
                String nomPrestataire = results.getString("nomPrestataire");
                String telePrestataire = results.getString("tele_Prestataire");
                String dateDebut = results.getDate("date_debut").toString();
                String dateFin = results.getDate("date_fin").toString();
                String dateArchive = results.getDate("date_archive").toString();
               
                String matierePrestataire = results.getString("metier");  // R�cup�rer "metier" comme "matierePrestataire"
                double prix = results.getDouble("prix");
                String statut = results.getString("statut");

                // Ajouter l'objet Historique � la liste
                historiqueList.add(new Historique(id, nomClient, teleClient, nomPrestataire, matierePrestataire, telePrestataire, dateDebut ,dateFin, dateArchive , prix, statut));
            }

            // Mettre � jour la TableView avec la liste des historiques
            hestoriqueTable.setItems(historiqueList);
            
           

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    
    @FXML
    void exporter(ActionEvent event) {
    	 try {
    		 
    		    System.out.println(historiqueList);
    	        // Pr�parer les donn�es de l'historique pour l'envoi au script PHP
    	        StringBuilder postData = new StringBuilder();
    	        for (Historique historique : historiqueList) {
    	            postData.append(historique.getNomClient()).append(",");
    	            postData.append(historique.getTelephoneClient()).append(",");
    	            postData.append(historique.getNomPrestataire()).append(",");
    	            postData.append(historique.getTelephonePrestataire()).append(",");
    	            postData.append(historique.getMatierePrestataire()).append(",");
    	            postData.append(historique.getDateDebut()).append(",");
    	            postData.append(historique.getDateFin()).append(",");
    	            postData.append(historique.getDateArchive()).append(",");
    	            postData.append(historique.getStatut()).append(",");
    	            postData.append(historique.getPrix()).append("\n");
    	            
    
    	        }
    	        

    	        // Cr�er le file chooser
    	        FileChooser fileChooser = new FileChooser();
    	        fileChooser.setTitle("Exporter l'historique au format Excel");
    	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));

    	        // Ouvrir le file chooser pour s�lectionner le fichier
    	        java.io.File file = fileChooser.showSaveDialog(null);
    	        if (file != null) {
    	            // URL du script PHP
    	            URL url = new URL("http://localhost/javafx_export/export_historique.php");
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	            conn.setRequestMethod("POST");
    	            conn.setDoOutput(true);
    	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    	            // Envoyer les donn�es au script PHP
    	            try (OutputStream os = conn.getOutputStream()) {
    	                os.write(postData.toString().getBytes("UTF-8"));
    	            }

    	            // T�l�charger le fichier Excel g�n�r�
    	            try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
    	                byte[] buffer = new byte[1024];
    	                int bytesRead;
    	                while ((bytesRead = in.read(buffer)) != -1) {
    	                    out.write(buffer, 0, bytesRead);
    	                }
    	                System.out.println("Le fichier Excel a �t� t�l�charg� avec succ�s � l'emplacement : " + file.getAbsolutePath());
    	                showAlert(Alert.AlertType.INFORMATION, "Exportation r�ussie", "L'historique a �t� export� avec succ�s.");
    	            }
    	        } else {
    	            System.out.println("Exportation annul�e par l'utilisateur.");
    	        }
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        System.err.println("Erreur lors de l'exportation : " + e.getMessage());
    	        showAlert(Alert.AlertType.ERROR, "Erreur d'exportation", "Une erreur est survenue lors de l'exportation de l'historique.");
    	    }
    	}
    private void searchHistorique(String searchText) {
        historiqueList.clear();

        String sql = "SELECT h.date_debut, h.date_fin, \r\n"
                + "       h.nomClient AS client_nom, h.tele_Client AS client_telephone, \r\n"
                + "       h.nomPrestataire AS prestataire_nom, h.tele_Prestataire AS prestataire_telephone, \r\n"
                + "       h.metier AS metier, h.prix, h.statut \r\n"
                + "FROM historique h \r\n"
                + "WHERE h.date_debut LIKE ? \r\n"
                + "   OR h.date_fin LIKE ? \r\n"
                + "   OR h.nomClient LIKE ? \r\n"
                + "   OR h.tele_Client LIKE ? \r\n"
                + "   OR h.nomPrestataire LIKE ? \r\n"
                + "   OR h.tele_Prestataire LIKE ? \r\n"
                + "   OR h.metier LIKE ? \r\n"
                + "   OR h.prix LIKE ? \r\n"
                + "   OR h.statut LIKE ?;";

        try (Connection connection = MysqlConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // D�finir les param�tres de la requ�te
            String searchPattern = "%" + searchText + "%";
            for (int i = 1; i <= 9; i++) { // 9 param�tres dans la requ�te
                ps.setString(i, searchPattern);
            }

            // Ex�cuter la requ�te
            ResultSet results = ps.executeQuery();

            // Traiter les r�sultats
            while (results.next()) {
                historiqueList.add(new Historique(
                        results.getString("client_nom"), // Pas de client_prenom dans votre table
                        results.getString("client_telephone"),
                        results.getString("prestataire_nom"), // Pas de prestataire_prenom dans votre table
                        results.getString("metier"),
                        results.getString("prestataire_telephone"),
                        results.getString("date_debut"),
                        results.getString("date_fin"),
                        results.getDouble("prix"),
                        results.getString("statut")
                ));
            }

            // Mettre � jour la table
            hestoriqueTable.setItems(historiqueList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de recherche", "Une erreur est survenue lors de la recherche.");
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
    
    
    private static int ITEMS_PER_PAGE = 10;

 // Charger une page sp�cifique pour l'historique
 private void loadPage(int pageIndex) {
     int fromIndex = pageIndex * ITEMS_PER_PAGE;
     int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, historiqueList.size());

     if (fromIndex > historiqueList.size()) {
         return; 
         // Pas de donn�es � charger pour cet index
     }

     ObservableList<Historique> pageData = FXCollections.observableArrayList(historiqueList.subList(fromIndex, toIndex));

     // Mettre � jour la TableView avec les donn�es de la page actuelle
     hestoriqueTable.setItems(pageData);
 }

 // Filtrer les historiques en fonction de la recherche
 private void filterHistorique(String searchQuery) {
     ObservableList<Historique> filteredList = FXCollections.observableArrayList();

     for (Historique historique : historiqueList) {
         if (historique.getNomClient().toLowerCase().contains(searchQuery) ||
             historique.getTelephoneClient().toLowerCase().contains(searchQuery) ||
             historique.getNomPrestataire().toLowerCase().contains(searchQuery) ||
             historique.getTelephonePrestataire().toLowerCase().contains(searchQuery) ||
             historique.getStatut().toLowerCase().contains(searchQuery)) {
             filteredList.add(historique);
         }
     }

     // Mettre � jour le nombre de pages pour la pagination
     paginationID.setPageCount((int) Math.ceil(filteredList.size() / (double) ITEMS_PER_PAGE));

     // Charger la premi�re page des r�sultats filtr�s
     loadFilteredPage(0, filteredList);
 }

 // Charger une page sp�cifique des r�sultats filtr�s
 private void loadFilteredPage(int pageIndex, ObservableList<Historique> filteredList) {
     int fromIndex = pageIndex * ITEMS_PER_PAGE;
     int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredList.size());

     if (fromIndex > filteredList.size()) {
         return; // Pas de donn�es � charger pour cet index
     }

     ObservableList<Historique> pageData = FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));

     // Mettre � jour la TableView avec les donn�es filtr�es pour la page actuelle
     hestoriqueTable.setItems(pageData);
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
     searchTextFieldID.textProperty().addListener((observable, oldValue, newValue) -> {
         String searchQuery = newValue.toLowerCase().trim();
         if (searchQuery.isEmpty()) {
             paginationID.setPageCount((int) Math.ceil(historiqueList.size() / (double) ITEMS_PER_PAGE));
             loadPage(0); // Recharger la premi�re page des donn�es non filtr�es
         } else {
             filterHistorique(searchQuery);
         }
     });
 }

 // Configuration de la s�lection de la taille des pages
 private void setupPageSizeChoiceBox() {
     // Ajouter les options de tailles de page
     pageSizeChoiceBox.setItems(FXCollections.observableArrayList(10, 20, 50, 100));
     pageSizeChoiceBox.setValue(ITEMS_PER_PAGE); // Valeur par d�faut

     // Ajouter un �v�nement pour mettre � jour la pagination lorsqu'une nouvelle taille est s�lectionn�e
     pageSizeChoiceBox.setOnAction(event -> {
         ITEMS_PER_PAGE = pageSizeChoiceBox.getValue();
         paginationID.setCurrentPageIndex(0); // R�initialiser � la premi�re page
         updatePagination(); // Mettre � jour la pagination
     });
 }

 // M�thode pour mettre � jour la pagination en fonction de la taille des pages
 private void updatePagination() {
     // Calculer le nombre total de pages
     int pageCount = (int) Math.ceil(historiqueList.size() / (double) ITEMS_PER_PAGE);
     paginationID.setPageCount(pageCount);

     // Charger les donn�es pour la page actuelle
     loadPage(paginationID.getCurrentPageIndex());
 }

 // M�thode pour charger une page sp�cifique en fonction de la taille des pages


 }

    
    
    
    
    
    
    
    
    
    
    
    
    









































