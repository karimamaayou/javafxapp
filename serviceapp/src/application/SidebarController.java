package application;

import java.io.IOException;
import java.util.Optional;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;




public class SidebarController {
	
    @FXML
    private FontAwesomeIcon icon1;

    @FXML
    private FontAwesomeIcon icon2;

    @FXML
    private FontAwesomeIcon icon3;

    @FXML
    private FontAwesomeIcon icon4;

    @FXML
    private FontAwesomeIcon icon5;
    
    @FXML
    private Button logOutButtonID;
    @FXML
    private Button viewDashBoardButtonID;
    @FXML
    private Button viewHistoriqueButtonID;
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
    
    
    if (activeButton.equals("viewDashBoard")) {
    	viewDashBoardButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
    	icon1.setFill(Color.web("#120cc9"));
    }
    else if (activeButton.equals("viewClients")) {
        viewClientsButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
        icon2.setFill(Color.web("#120cc9"));
    } else if (activeButton.equals("viewPrestatiares")) {
        viewPrestatiaresButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
        icon3.setFill(Color.web("#120cc9"));
    } else if (activeButton.equals("viewReservations")) {
        viewReservationsButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
        icon4.setFill(Color.web("#120cc9"));
    } else if (activeButton.equals("viewHistorique")) {
    	viewHistoriqueButtonID.setStyle("-fx-background-color: #FFFFFF;-fx-text-fill: #120cc9;");
    	icon5.setFill(Color.web("#120cc9"));
    }
    
    iconHover();
    
    	
    }
    
    private static String activeButton = "viewDashBoard";
    // Method to reset the styles of all buttons

    //used to implement the hover effect on icons
    private void iconHover() {
    	if(!activeButton.equals("viewDashBoard")) {
    		
        	viewDashBoardButtonID.setOnMouseEntered(event -> icon1.setFill(Color.web("#120cc9")));
        	viewDashBoardButtonID.setOnMouseExited(event -> icon1.setFill(Color.web("#ffffff")));
    	}
    	if(!activeButton.equals("viewClients")) {
    		
    	    viewClientsButtonID.setOnMouseEntered(event -> icon2.setFill(Color.web("#120cc9")));
    	    viewClientsButtonID.setOnMouseExited(event -> icon2.setFill(Color.web("#ffffff")));
    	}
    	if(!activeButton.equals("viewPrestatiares")) {
    		
         	viewPrestatiaresButtonID.setOnMouseEntered(event -> icon3.setFill(Color.web("#120cc9")));
    	    viewPrestatiaresButtonID.setOnMouseExited(event -> icon3.setFill(Color.web("#ffffff")));
    	}
    	if(!activeButton.equals("viewReservations")) {
    		
    	    viewReservationsButtonID.setOnMouseEntered(event -> icon4.setFill(Color.web("#120cc9")));
         	viewReservationsButtonID.setOnMouseExited(event -> icon4.setFill(Color.web("#ffffff")));
    	}
    	if(!activeButton.equals("viewHistorique")) {
    		
    	    viewHistoriqueButtonID.setOnMouseEntered(event -> icon5.setFill(Color.web("#120cc9")));
    	    viewHistoriqueButtonID.setOnMouseExited(event -> icon5.setFill(Color.web("#ffffff")));
    	}
    }

    @FXML
    void viewDashBoardButton(ActionEvent event) {
    	
    	activeButton = "viewDashBoard";
    	DBUtils.changeScene( event, "dashboard.fxml");

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
    void viewHistoriqueButton(ActionEvent event) {
    	
    	activeButton = "viewHistorique";
    	DBUtils.changeScene( event, "historiqueTable.fxml");
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
	
		

    }



}
