
package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
          
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("parametre.fxml"));      
            HBox root = loader.load(); 
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("table.css").toExternalForm());           
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
