package application;

import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SidebarController {

    @FXML
    private Button logOutButtonID;

    @FXML
    private Button viewClientsButtonID;

    @FXML
    private Button viewPrestatiaresButtonID;

    @FXML
    private Button viewReservationsButtonID;

    @FXML
    private ImageView logo;


    @FXML
    public void initialize() {
    
    Image logoImage = new Image(getClass().getResource("resources/images/logo.png").toExternalForm());
    logo.setImage(logoImage);
    
    resetButtonStyles();
    if (activeButton.equals("viewClients")) {
        viewClientsButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
    } else if (activeButton.equals("viewPrestatiares")) {
        viewPrestatiaresButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
    } else if (activeButton.equals("viewReservations")) {
        viewReservationsButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
    }
    	
    }
    
    private static String activeButton = "viewReservations";
    // Method to reset the styles of all buttons
    private void resetButtonStyles() {

        viewClientsButtonID.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        viewPrestatiaresButtonID.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        viewReservationsButtonID.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        
        

    }
    
    @FXML
    void viewClientsButton(ActionEvent event) {
    	
    	activeButton = "viewClients";
    	DBUtils.changeScene( event, "clientsTable.fxml");

    }
    
    @FXML
    void viewPrestatairesButton(ActionEvent event) {
    	
    	activeButton = "viewPrestatiares";
    	DBUtils.changeScene( event, "prestataireTable.fxml");

    }
    
    @FXML
    void viewReservationsButton(ActionEvent event) {
  
    	activeButton = "viewReservations";
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
		

    }

}
