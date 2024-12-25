package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ClientTableController {

    @FXML
    private TableView<Client> clientTable;
    
    @FXML
    private Button ajouterButtonID;

    @FXML
    private TableColumn<Client,String> emailColumn;

    @FXML
    private TableColumn<Client, String> nomColumn;

    @FXML
    private TableColumn<Client, String> prenomColumn;
 
    
    @FXML
    private TableColumn<Client, String> telephoneColumn;

    @FXML
    private TableColumn<Client, String> villeColumn;
    
    @FXML
    void ajouterClient(ActionEvent event) { 
    	
    
    	try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addClientsFrom.fxml"));
            HBox root = loader.load();

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ajouterButtonID.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    
    	

    }
    
    
    
    
    
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    
    public void initialize() {
    	
    	emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    	nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
    	prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
    	telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    	villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
    	
    	getAllClients();
    	
    }
    
    //get all the clients from the database
    public void getAllClients() {
    	
    	clientList.clear();

		Connection connection = MysqlConnection.getDBConnection();

		String sql = "Select client_id,nom,prenom,email,ville,telephone from client join ville using(ville_id);";
      
		
		try {

			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet results = ps.executeQuery();
			
			while (results.next()) {
				int id = results.getInt("client_id");
				String nom = results.getString("nom");
				String prenom = results.getString("prenom");
				String email = results.getString("email");
				String ville = results.getString("ville");
				String telephone = results.getString("telephone");
				
				clientList.add(new Client(id, nom, prenom, email,ville,telephone));
		
			    clientTable.setItems(clientList);
			
			    

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
}
