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
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String favChannel) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            if (favChannel != null) {
                LoggedInController loggedInController = loader.getController();
                loggedInController.setUserInformation(username, favChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Changement de sc�ne
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}