package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        paginationID.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> 
            loadFilteredPage(newValue.intValue(), filteredList));
    }

    private void loadFilteredPage(int pageIndex, ObservableList<Reservation> filteredList) {
        int pageSize = pageSizeChoiceBox.getValue(); // Taille de la page sélectionnée
        int fromIndex = pageIndex * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredList.size());
        ObservableList<Reservation> pageData = FXCollections.observableArrayList(filteredList.subList(fromIndex, toIndex));

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
        String sql = "SELECT c.client_id, CONCAT(c.nom, ' ', c.prenom) AS nomCompletClient, "
                + "c.telephone AS telephoneClient, p.prestataire_id, CONCAT(p.nom, ' ', p.prenom) AS nomCompletPrestataire, "
                + "p.telephone AS telephonePrestataire, r.reservation_id, r.date_debut as dateDebut, r.date_fin as dateFin, "
                + "s.statut, r.prix FROM client c "
                + "JOIN reservation r ON c.client_id = r.client_id "
                + "JOIN prestataire p ON p.prestataire_id = r.prestataire_id "
                + "JOIN statut s ON s.statut_id = r.statut_id;";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                reservationList.add(new Reservation(
                        results.getInt("client_id"),
                        results.getString("nomCompletClient"),
                        results.getString("telephoneClient"),
                        results.getInt("prestataire_id"),
                        results.getString("nomCompletPrestataire"),
                        results.getString("telephonePrestataire"),
                        results.getInt("reservation_id"),
                        results.getString("statut"),
                        results.getString("dateDebut"),
                        results.getString("dateFin"),
                        results.getDouble("prix")));
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
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette réservation ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String query = "DELETE FROM reservation WHERE reservation_id = ?";
            try (Connection connection = MysqlConnection.getDBConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, selectedReservation.getIdReservtion());
                ps.executeUpdate();
                reservationList.remove(selectedReservation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void changeStatut(ActionEvent event) {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert(Alert.AlertType.WARNING, "Changement de statut impossible", "Aucune réservation sélectionnée !");
            return;
        }
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Changer le statut");
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList("Terminer", "Annuler", "En cours"));
        comboBox.setValue(selectedReservation.getStatut());
        dialog.getDialogPane().setContent(comboBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> button == ButtonType.OK ? comboBox.getValue() : null);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatut -> updateReservationStatut(selectedReservation, newStatut));
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter vers Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        java.io.File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write("Exemple d'export".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
