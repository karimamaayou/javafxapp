package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoggedInController {

    @FXML
    private Label labelWelcome;

    private String username;  

  
    public void setUserInformation(String username, String favChannel) {
     
        this.username = username;
        labelWelcome.setText("Bienvenue, " + username);
    }
}
