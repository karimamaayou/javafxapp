package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PrestataireTableController {

    @FXML
    private Button ajouterButtonID;

    @FXML
    private TableColumn<?, ?> descriptionColumn;

    @FXML
    private TableColumn<?, ?> disponibiliteColumn;

    @FXML
    private TableColumn<?, ?> emailColumn;

    @FXML
    private Button modifierButtonID;

    @FXML
    private TableColumn<?, ?> nomColumn;

    @FXML
    private TableColumn<?, ?> prenomColumn;

    @FXML
    private TableView<?> prestataireTable;

    @FXML
    private TableColumn<?, ?> prix_maxColumn;

    @FXML
    private TableColumn<?, ?> prix_minColumn;

    @FXML
    private Button supprimerButtonID;

    @FXML
    private TableColumn<?, ?> telephonePrestataireColumn;

    @FXML
    private TableColumn<?, ?> villeColumn;

    @FXML
    void ajouterPrestataire(ActionEvent event) {

    }

    @FXML
    void modifierPrestataire(ActionEvent event) {

    }

    @FXML
    void supprimerPrestataire(ActionEvent event) {

    }

}
