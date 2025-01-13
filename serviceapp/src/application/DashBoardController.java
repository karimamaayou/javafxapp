package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;

public class DashBoardController {

    @FXML
    private BarChart<String,Integer> barChartID;
    @FXML
    private LineChart<String,Integer> lineChartID;
    @FXML
    private PieChart pieChartPrestataire;
    @FXML
    private PieChart pieChartReservationStatut;


    @FXML
    private PieChart pieChartID;
    
    
    public void initialize() {
    	
    	//----------------------------LineChar of Reservations-----------------------//
    	
        // Fetch reservation data over time (evolution of reservations)
        Map<String, Integer> reservationsMap = getReservationsEvolution();
        
        // Define X and Y axes
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre de Réservations");  // Y-axis label (Total Reservations)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mois");  // X-axis label (Months)

        // Populate the LineChart with data for reservations
        XYChart.Series<String, Integer> reservationsSeries = new XYChart.Series<>();
        reservationsSeries.setName("Évolution des réservations");

        for (Map.Entry<String, Integer> entry : reservationsMap.entrySet()) {
            String month = entry.getKey();  // Mois
            int totalReservations = entry.getValue();
            reservationsSeries.getData().add(new XYChart.Data<>(month, totalReservations));
        }


        // Clear existing data in the LineChart
        lineChartID.getData().clear();

        // Add both series to the LineChart
        lineChartID.getData().add(reservationsSeries);
        
        //-----------------------------------------------------------------------//
        
    	//----------------------------BarChart of reservations-----------------------//
        
        // Fetch reservation data by "metier"
        Map<String, Integer> reservationsPerMetier = getReservationsByMetier();

        // Populate the BarChart with data
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Reservations per Metier");

        // Add data to the series
        for (Map.Entry<String, Integer> entry : reservationsPerMetier.entrySet()) {
            String metier = entry.getKey();
            int count = entry.getValue();
            series.getData().add(new XYChart.Data<>(metier, count));
        }
        // Clear any existing data in the BarChart
        barChartID.getData().clear();

        // Add the series to the BarChart
        barChartID.getData().add(series);
        
        //-----------------------------------------------------------------------//
        
        //----------------------------PieChart of clients-----------------------//
        
    	
        // Fetch the client data and count clients by city
        Map<String, Integer> cityCountMap = getClientsByCity();
        
        // Populate the PieChart with data
        pieChartID.getData().clear();  // Clear existing data
        for (Map.Entry<String, Integer> entry : cityCountMap.entrySet()) {
            String city = entry.getKey();
            int count = entry.getValue();
            pieChartID.getData().add(new PieChart.Data(city + " (" + count + ")", count));
        }
        
       //-----------------------------------------------------------------------//
        //----------------------------PieChart of prestataire-----------------------//
        
    	
        // Fetch the prestataire data and count prestataire by city
        Map<String, Integer> prestataireByCity = getPrestataireByCity();
        
        // Populate the PieChart with data
        pieChartPrestataire.getData().clear();  // Clear existing data
        for (Map.Entry<String, Integer> entry : prestataireByCity.entrySet()) {
            String city = entry.getKey();
            int count = entry.getValue();
            pieChartPrestataire.getData().add(new PieChart.Data(city + " (" + count + ")", count));
        }
        
       //-----------------------------------------------------------------------//
       //----------------------------PieChart of Reservations-------------------//
        
    	
        // Fetch the statut data and count reservation by statut
        Map<String, Integer> statutCount = getStatutByReservation();
        
        // Populate the PieChart with data
        pieChartReservationStatut.getData().clear();  // Clear existing data
        for (Map.Entry<String, Integer> entry : statutCount.entrySet()) {
            String statut = entry.getKey();
            int count = entry.getValue();
            pieChartReservationStatut.getData().add(new PieChart.Data(statut + " (" + count + ")", count));
        }
        
       //-----------------------------------------------------------------------//
        


    	
    }
    
    @FXML
    void ajouterClient(ActionEvent event) {

    	DBUtils.changeScene( event, "addClientsFrom.fxml");
    }

    @FXML
    void ajouterPrestataire(ActionEvent event) {
 
    	DBUtils.changeScene( event, "addPrestataireForm.fxml");
    }

    @FXML
    void ajouterReservation(ActionEvent event) {
    	
    	DBUtils.changeScene( event, "addReservationForm.fxml");

    }
    
    public Map<String, Integer> getReservationsEvolution() {
        Map<String, Integer> reservationsMap = new HashMap<>();
        Connection connection = MysqlConnection.getDBConnection();
        
        String sql = """
            SELECT 
                EXTRACT(MONTH FROM r.date_debut) AS month,
                COUNT(r.reservation_id) AS total_reservations
            FROM 
                reservation r
            GROUP BY 
                EXTRACT(MONTH FROM r.date_debut)
            ORDER BY
                month;
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet results = ps.executeQuery();

            while (results.next()) {
                String month = results.getString("month");
                int totalReservations = results.getInt("total_reservations");
                reservationsMap.put(month, totalReservations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservationsMap;
    }
    
    public Map<String,Integer > getPrestataireByCity() {
    	
    	Map<String,Integer> prestataireParVille= new HashMap<>();
    	Connection connection = MysqlConnection.getDBConnection();
		String sql = "Select ville,count(prestataire_id) as nbr from prestataire join ville using(ville_id) group by ville;";

		try {

			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet results = ps.executeQuery();

			while (results.next()) {
				
				String ville = results.getString("ville");
				int nombreVille = results.getInt("nbr");
				prestataireParVille.put(ville,nombreVille);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return prestataireParVille;
    }
   
    public Map<String,Integer > getClientsByCity() {
    	
    	Map<String,Integer> clientsParVille= new HashMap<>();
    	Connection connection = MysqlConnection.getDBConnection();
		String sql = "Select ville,count(client_id) as nbr from client join ville using(ville_id) group by ville;";

		try {

			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet results = ps.executeQuery();

			while (results.next()) {
				
				String ville = results.getString("ville");
				int nombreVille = results.getInt("nbr");
				clientsParVille.put(ville,nombreVille);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return clientsParVille;
    }
    public Map<String,Integer> getStatutByReservation(){
    	Map<String,Integer> statutParReservation= new HashMap<>();
    	Connection connection = MysqlConnection.getDBConnection();
		String sql = "Select statut,count(reservation_id) as nbr from reservation join statut using(statut_id) group by statut;";

		try {

			PreparedStatement ps = connection.prepareStatement(sql);

			ResultSet results = ps.executeQuery();

			while (results.next()) {
				
				String statut = results.getString("statut");
				int nombreStatut = results.getInt("nbr");
				statutParReservation.put(statut,nombreStatut);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return statutParReservation;
    }
    public Map<String, Integer> getReservationsByMetier() {
    	
        Map<String, Integer> reservationsPerMetier = new HashMap<>();
        Connection connection = MysqlConnection.getDBConnection();
        
        // SQL query to count reservations per "metier"
        String sql = """
            SELECT 
                m.metier AS metier,
                COUNT(r.reservation_id) AS total_reservations
            FROM 
                reservation r
            JOIN 
                prestataire p ON r.prestataire_id = p.prestataire_id
            JOIN 
                metier m ON p.metier_id = m.metier_id
            GROUP BY 
                m.metier
            LIMIT  4;
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet results = ps.executeQuery();

            // Process the results
            while (results.next()) {
                String metier = results.getString("metier");
                int totalReservations = results.getInt("total_reservations");
                reservationsPerMetier.put(metier, totalReservations);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservationsPerMetier;
    }
 

}
