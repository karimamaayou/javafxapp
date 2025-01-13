package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DBUtils {

    // Cette méthode permet de changer de scène
    public static void changeScene(ActionEvent event, String fxmlFile) {
    	
    	String title= "Bladna Services";
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Changement de scène
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        Scene currentScene = ((Node) event.getSource()).getScene();
        Scene newScene = new Scene(root,currentScene.getWidth(),currentScene.getHeight());
        stage.setTitle(title);
        stage.setScene(newScene);
        newScene.getStylesheets().add(DBUtils.class.getResource("table.css").toExternalForm()); // Ajouter les styles CSS
        
        stage.setMaximized(true);
        
        stage.centerOnScreen();
        
        
        stage.show();
    }
}