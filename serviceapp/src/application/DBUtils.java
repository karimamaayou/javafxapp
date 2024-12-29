package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DBUtils {

    // Cette m�thode permet de changer de sc�ne
    public static void changeScene(ActionEvent event, String fxmlFile) {
    	
    	String title= "Bladna Services";
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Changement de sc�ne
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 800, 500));
        stage.centerOnScreen();
        stage.show();
    }
}