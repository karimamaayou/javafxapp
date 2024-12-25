package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReservationTableController {

    @FXML
    private Button ajouterButtonID;

    @FXML
    private TableColumn<?, ?> dateDebutColumn;

    @FXML
    private TableColumn<?, ?> dateFinColumn;

    @FXML
    private TableColumn<?, ?> nomCompleteClientColumn;

    @FXML
    private TableColumn<?, ?> nomCompletePrestataireColumn;

    @FXML
    private TableView<?> prestataireTable;

    @FXML
    private Button statutButtonID;

    @FXML
    private TableColumn<?, ?> statutColumn;

    @FXML
    private TableColumn<?, ?> telephoneClientColumn;

    @FXML
    private TableColumn<?, ?> telephonePrestataireColumn;

    @FXML
    void ajouterReservation(ActionEvent event) {

    }

    @FXML
    void changeStatut(ActionEvent event) {

    }

}
