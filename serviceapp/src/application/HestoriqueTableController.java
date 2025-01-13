package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HestoriqueTableController {

    @FXML
    private TableColumn<?, ?> dateDebutColumn;

    @FXML
    private TableColumn<?, ?> dateFinColumn;

    @FXML
    private Button exporterButtonID;

    @FXML
    private TableColumn<?, ?> matierPrestataireColumn;

    @FXML
    private TableColumn<?, ?> nomCompleteClientColumn;

    @FXML
    private TableColumn<?, ?> nomCompletePrestataireColumn;

    @FXML
    private ChoiceBox<?> pageSizeChoiceBox;

    @FXML
    private Pagination paginationID;

    @FXML
    private TableColumn<?, ?> prix_Column;

    @FXML
    private TableView<?> reservationTable;

    @FXML
    private TextField searchTextFieldID;

    @FXML
    private Button statutButtonID;

    @FXML
    private TableColumn<?, ?> statutColumn;

    @FXML
    private TableColumn<?, ?> telephoneClientColumn;

    @FXML
    private TableColumn<?, ?> telephonePrestataireColumn;

    @FXML
    void changeStatut(ActionEvent event) {

    }

    @FXML
    void exporter(ActionEvent event) {

    }

}
