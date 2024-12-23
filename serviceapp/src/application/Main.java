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
          
            // Charger le fichier FXML avec un HBox comme racine
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));
            HBox root = loader.load(); // Charger l'FXML dans un HBox

            // Cr�er la sc�ne et ajouter le HBox
            Scene scene = new Scene(root, 400, 400); // D�finir la taille de la sc�ne
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); // Ajouter les styles CSS

            // D�finir la sc�ne et afficher la fen�tre
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
