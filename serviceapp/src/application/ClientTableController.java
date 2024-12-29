package application;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ClientTableController {

	@FXML
	private TableView<Client> clientTable;

	@FXML
	private Button ajouterButtonID;
	@FXML
	private Button modifierButtonID;
	@FXML
	private Button supprimerButtonID;
    @FXML
    private Button exporterButtonID;
    @FXML
    private TextField searchField; 
    @FXML
    private Button logOutButtonID;
    
    @FXML
    private Button viewClientsButtonID;

    @FXML
    private Button viewPrestatiaresButtonID;

    @FXML
    private Button viewReservationsButtonID;

	@FXML
	private TableColumn<Client, String> emailColumn;

	@FXML
	private TableColumn<Client, String> nomColumn;

	@FXML
	private TableColumn<Client, String> prenomColumn;

	@FXML
	private TableColumn<Client, String> telephoneColumn;

	@FXML
	private TableColumn<Client, String> villeColumn;

	private ObservableList<Client> clientList = FXCollections.observableArrayList();
	
	public void initialize() {
		

		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
		prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
		villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));

		getAllClients();
		
		// Add a listener to the search field for reactive search
	    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	        String searchQuery = newValue.toLowerCase().trim();
	        if (searchQuery.isEmpty()) {
	            // If the search field is empty, show all clients
	            clientTable.setItems(clientList);
	        } else {
	            // Otherwise, filter the clients based on the search query
	            filterClients(searchQuery);
	        }
	    });

	}

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

	@FXML
	void modifierClient(ActionEvent event) {
		
		Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
		//if no client is selected show warning 
		if (selectedClient == null) {
	        // Show a warning dialog if no client is selected
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun client sélectionné");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez sélectionner un client à modifier.");
			warningAlert.showAndWait();
			return;
	    }
		
	    try {
	        // Load the modifier window
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("modifierClientForm.fxml"));
	        Stage stage = new Stage();
	        stage.setScene(new Scene(loader.load()));

	        // Pass the selected client to the modifier window
	        ModifierClientController controller = loader.getController();
	        controller.setSelectedClient(selectedClient);

	        // Show the modifier window
	        stage.showAndWait();

	        // Refresh the table after modification
	        clientTable.refresh();
	        
	        //Refrech the ville Column since it related to other table
	        getAllClients();
	        
	        

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
		

	

	@FXML
	void exporter(ActionEvent event) {
	    try {
	        // Prepare data to send to the PHP script
	        StringBuilder postData = new StringBuilder();
	        for (Client client : clientList) {
	            postData.append(client.getNom()).append(",");
	            postData.append(client.getPrenom()).append(",");
	            postData.append(client.getEmail()).append(",");
	            postData.append(client.getTelephone()).append(",");
	            postData.append(client.getVille()).append("\n");
	        }

	        // Create file chooser
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Save Excel File");
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

	        // Open file chooser to get the selected file
	        java.io.File file = fileChooser.showSaveDialog(null);
	        if (file != null) {
	            URL url = new URL("http://localhost/javafx_export/export_excel.php");
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	            try (OutputStream os = conn.getOutputStream()) {
	                os.write(postData.toString().getBytes("UTF-8"));
	            }

	            // Handle response and download the file
	            try (InputStream in = conn.getInputStream();
	                 FileOutputStream out = new FileOutputStream(file)) {
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	                System.out.println("Excel file downloaded successfully to " + file.getAbsolutePath());
	            }
	        } else {
	            System.out.println("File save cancelled by user.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Error during file export: " + e.getMessage());
	    }
	}



	@FXML
	void supprimerClient(ActionEvent event) {
		// Get the selected client from the table
		Client selectedClient = clientTable.getSelectionModel().getSelectedItem();

		//if client is selected
		if (selectedClient != null) {
			// Show confirmation dialog
			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation de suppression");
			confirmationAlert.setHeaderText(null);
			confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce client ?");

			// Wait for the user to respond
			Optional<ButtonType> result = confirmationAlert.showAndWait();
            //if he chosed ok
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// User confirmed, delete the client
				deleteClientFromDatabase(selectedClient);
				clientList.remove(selectedClient);
			} 
			//if he chosed cancel clear the selection in table
			else {
				// User cancelled, clear the selection
				clientTable.getSelectionModel().clearSelection();
			}
		}
		//if no client is selected
		else {
			// Show a warning if no client is selected
			Alert warningAlert = new Alert(Alert.AlertType.WARNING);
			warningAlert.setTitle("Aucun client sélectionné");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Veuillez sélectionner un client à supprimer.");
			warningAlert.showAndWait();
		}

	}


	// get all the clients from the database
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

				clientList.add(new Client(id, nom, prenom, email, ville, telephone));

				clientTable.setItems(clientList);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		else {
			// User cancelled, clear the selection
			clientTable.getSelectionModel().clearSelection();
		}

    }
	
	private void filterClients(String searchQuery) {
	    ObservableList<Client> filteredList = FXCollections.observableArrayList();

	    for (Client client : clientList) {
	        if (client.getNom().toLowerCase().contains(searchQuery) || 
	            client.getPrenom().toLowerCase().contains(searchQuery) || 
	            client.getEmail().toLowerCase().contains(searchQuery) || 
	            client.getTelephone().toLowerCase().contains(searchQuery)) {
	            filteredList.add(client);
	        }
	    }
	    
	    clientTable.setItems(filteredList);
	}



    @FXML
    void viewClientsButton(ActionEvent event) {
    	
    	DBUtils.changeScene( event, "clientsTable.fxml");

    }
    
    @FXML
    void viewPrestatairesButton(ActionEvent event) {
  
    	DBUtils.changeScene( event, "prestataireTable.fxml");

    }
    
    @FXML
    void viewReservationsButton(ActionEvent event) {
  
    	DBUtils.changeScene( event, "reservationTable.fxml");

    }

 
	private void deleteClientFromDatabase(Client client) {
		Connection connection = MysqlConnection.getDBConnection();
		String sql = "DELETE FROM client WHERE client_id = ?";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, client.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
