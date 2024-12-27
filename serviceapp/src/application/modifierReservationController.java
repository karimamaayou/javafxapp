package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class modifierReservationController {

	  @FXML
	    private Button modifierButtonId;

    @FXML
    private ComboBox<String> clientField; //you need a comobox

    private Map<String, Integer> clientMap = new HashMap<>();// with the comobox you need a Map list to link the id's with the clients
    private Map<String, Integer> prestataireMap = new HashMap<>();
    @FXML
    private ComboBox<String> prestataireField;

    @FXML
    private Label clientErrorLabel;

    @FXML
    private Label dateDebutErrorLabel;

    @FXML
    private Label dateDebutErrorLabel1;

    @FXML
    private TextField dateDebutField;

    @FXML
    private Label dateFinErrorLabel;

    @FXML
    private TextField dateFinField;

    @FXML
    private Label prestataireErrorLabel;

	

	private Reservation selectedReservation;
    @FXML
    public void initialize() {
        // Clear error messages when a selection is made or text is entered
    	clientField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                clientErrorLabel.setText(""); // Clear client error label
            }
        });

    	prestataireField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                prestataireErrorLabel.setText(""); // Clear prestataire error label
            }
        });

        dateDebutField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                dateDebutErrorLabel.setText(""); // Clear dateDebut error label
                dateDebutField.getStyleClass().remove("error"); // Remove "error" style
            }
        });

        dateFinField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                dateFinErrorLabel.setText(""); // Clear dateFin error label
                dateFinField.getStyleClass().remove("error"); // Remove "error" style
            }
        });
        
        
        loadClientFromDatabase(); //you need this to show the client in the comobox
        loadPrestataireFromDatabase();
    }
    @FXML
    void modifierReservation(ActionEvent event) {
    	  boolean hasError = false;

          // Clear previous error messages
          clientErrorLabel.setText("");
          prestataireErrorLabel.setText("");
          dateDebutErrorLabel.setText("");
          dateFinErrorLabel.setText("");

          // V�rification du champ Client
          if (clientField.getSelectionModel().isEmpty()) {
              clientErrorLabel.setText("Veuillez s�lectionner un client.");
              hasError = true;
          }

          // V�rification du champ Prestataire
          if (prestataireField.getSelectionModel().isEmpty()) {
              prestataireErrorLabel.setText("Veuillez s�lectionner un prestataire.");
              hasError = true;
          }

          // V�rification du champ Date de d�but
          if (dateDebutField.getText().trim().isEmpty()) {
              dateDebutErrorLabel.setText("Veuillez entrer la date de d�but.");
              dateDebutField.getStyleClass().add("error"); // Add error style to the field
              hasError = true;
          }

          // V�rification du champ Date de fin
          if (dateFinField.getText().trim().isEmpty()) {
              dateFinErrorLabel.setText("Veuillez entrer la date de fin.");
              dateFinField.getStyleClass().add("error"); // Add error style to the field
              hasError = true;
          }
          
          
          
          
          
          if (!hasError) {
              try (Connection connection = MysqlConnection.getDBConnection()) {
                  String sql = "UPDATE reservation SET client_id = ?, prestataire_id = ?, date_debut = ?, date_fin = ? WHERE reservation_id = ?";
                  PreparedStatement ps = connection.prepareStatement(sql);

                  int selectedClientId = clientMap.get(clientField.getSelectionModel().getSelectedItem());
                  int selectedPrestataireId = prestataireMap.get(prestataireField.getSelectionModel().getSelectedItem());

                  ps.setInt(1, selectedClientId);
                  ps.setInt(2, selectedPrestataireId);
                  ps.setString(3, dateDebutField.getText());
                 
                  ps.setString(4, dateFinField.getText());
                  ps.setInt(5,selectedReservation.getIdReservtion());
                  
                  System.out.println(selectedReservation.getIdReservtion());
            
                  int rowsAffected = ps.executeUpdate();//Recuperer le nombre de ligne affecter

                  if (rowsAffected > 0) {
                      System.out.println("R�servation modifi�e avec succ�s.");
                       // Fermer la fen�tre apr�s la mise � jour
                      Stage stage = (Stage) dateDebutField.getScene().getWindow();
                      stage.close();

                  } else {
                      System.err.println("Aucune r�servation n'a �t� modifi�e. V�rifiez l'ID de la r�servation.");
                       // G�rer le cas o� aucune ligne n'a �t� affect�e (par exemple, ID incorrect)
                  }

              } catch (SQLException e) {
                  e.printStackTrace();
                   // Afficher un message d'erreur plus convivial � l'utilisateur
                  System.err.println("Erreur lors de la modification de la r�servation : " + e.getMessage());

              }
          }
          
          
          
          
          
          
          
       
    }
    
    private void loadClientFromDatabase() {
    	
        try (Connection conn = MysqlConnection.getDBConnection()) {
            String query = "SELECT client_id,concat(nom,' ',prenom) as nomCompleteClient FROM client"; // Requ�te pour r�cup�rer les m�tiers

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

            	clientField.getItems().clear(); // Vide la liste avant de la remplir avec les nouveaux �l�ments

                while (rs.next()) {
                	int client_id = rs.getInt("client_id");
                    String nomCompleteCLient = rs.getString("nomCompleteClient");
                    clientField.getItems().add(nomCompleteCLient); // Ajoute chaque client dans le ComboBox
                    clientMap.put(nomCompleteCLient,client_id);
                }

                System.out.println("client charg�s avec succ�s.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si elle survient
        }
    }
    private void loadPrestataireFromDatabase() {
        try (Connection conn = MysqlConnection.getDBConnection()) {
            String query = "SELECT prestataire_id, concat(nom, ' ', prenom) as nomCompletePrestataire FROM prestataire"; // Requ�te pour r�cup�rer les prestataires

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                prestataireField.getItems().clear(); // Vide la liste avant de la remplir avec les nouveaux �l�ments

                while (rs.next()) {
                    int prestataire_id = rs.getInt("prestataire_id");
                    String nomCompletePrestataire = rs.getString("nomCompletePrestataire");
                    prestataireField.getItems().add(nomCompletePrestataire); // Ajoute chaque prestataire dans le ComboBox
                    prestataireMap.put(nomCompletePrestataire, prestataire_id); // Associe le nom complet � l'ID
                }

                System.out.println("Prestataires charg�s avec succ�s.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur SQL si elle survient
        }
    }


	public void setSelectedReservation(Reservation selectedReservation) {
		
		
		
	    	 this.selectedReservation = selectedReservation;
	        
	        
	      
	        dateDebutField.setText(selectedReservation.getDateDebut());
	        dateFinField.setText(selectedReservation.getDateFin());
	        
	}
	

}
