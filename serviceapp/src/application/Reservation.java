package application;

public class Reservation {
    
    int idClient;
    String nomCompletClient;
    String telephoneClient;
    
    int idPrestataire;
    String nomCompletPrestataire;
    String telephonePrestataire;
    
    int idReservtion;
    String statut;
    String date_debutReservation;
    String date_finReservation;
    
    // Constructeur avec 12 paramètres modifiés
    public Reservation(int idClient, String nomCompletClient, String telephoneClient, int idPrestataire,
                        String nomCompletPrestataire, String telephonePrestataire, int idReservtion, 
                        String statut, String date_debutReservation, String date_finReservation) {
        
        this.idClient = idClient;
        this.nomCompletClient = nomCompletClient;
        this.telephoneClient = telephoneClient;
        this.idPrestataire = idPrestataire;
        this.nomCompletPrestataire = nomCompletPrestataire;
        this.telephonePrestataire = telephonePrestataire;
        this.idReservtion = idReservtion;
        this.statut = statut;
        this.date_debutReservation = date_debutReservation;
        this.date_finReservation = date_finReservation;
    }
    
    // Getters and Setters
    
    public int getIdClient() {
        return idClient;
    }
    
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
    
    public String getNomCompletClient() {
        return nomCompletClient;
    }
    
    public void setNomCompletClient(String nomCompletClient) {
        this.nomCompletClient = nomCompletClient;
    }
    
    public String getTelephoneClient() {
        return telephoneClient;
    }
    
    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }
    
    public int getIdPrestataire() {
        return idPrestataire;
    }
    
    public void setIdPrestataire(int idPrestataire) {
        this.idPrestataire = idPrestataire;
    }
    
    public String getNomCompletPrestataire() {
        return nomCompletPrestataire;
    }
    
    public void setNomCompletPrestataire(String nomCompletPrestataire) {
        this.nomCompletPrestataire = nomCompletPrestataire;
    }
    
    public String getTelephonePrestataire() {
        return telephonePrestataire;
    }
    
    public void setTelephonePrestataire(String telephonePrestataire) {
        this.telephonePrestataire = telephonePrestataire;
    }
    
    public int getIdReservtion() {
        return idReservtion;
    }
    
    public void setIdReservtion(int idReservtion) {
        this.idReservtion = idReservtion;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public String getDateDebut() {
        return date_debutReservation;
    }
    
    public void setDateDebut(String date_debutReservation) {
        this.date_debutReservation = date_debutReservation;
    }
    
    public String getDateFin() {
        return date_finReservation;
    }
    
    public void setDateFin(String date_finReservation) {
        this.date_finReservation = date_finReservation;
    }

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
