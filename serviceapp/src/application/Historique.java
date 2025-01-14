package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Historique {
    private int id;
    private String nomClient;
    private String telephoneClient;
    private String nomPrestataire;
    private String matierePrestataire; // Renommé en "matierePrestataire" pour correspondre à la colonne
    private String telephonePrestataire;
    private String dateDebut;
    private String dateFin;
    private String dateArchive;
    private double prix;
    private String statut;

    // Constructeur
    public Historique(int id, String nomClient, String telephoneClient, String nomPrestataire, String matierePrestataire, String telephonePrestataire, String dateDebut, String dateFin, String dateArchive , double prix, String statut )  {
        this.id = id;
        this.nomClient = nomClient;
        this.telephoneClient = telephoneClient;
        this.nomPrestataire = nomPrestataire;
        this.matierePrestataire = matierePrestataire;
        this.telephonePrestataire = telephonePrestataire;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.statut = statut;
         this.dateArchive = dateArchive ;
    }
    public Historique( String nomClient, String telephoneClient, String nomPrestataire, String matierePrestataire, String telephonePrestataire, String dateDebut, String dateFin, double prix, String statut) {
      
        this.nomClient = nomClient;
        this.telephoneClient = telephoneClient;
        this.nomPrestataire = nomPrestataire;
        this.matierePrestataire = matierePrestataire;
        this.telephonePrestataire = telephonePrestataire;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.statut = statut;
    }
    public String getDateArchive() {
        return dateArchive;
    }

 
    public void setDateArchive(String dateArchive) {
        this.dateArchive = dateArchive;
    }
   
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomClient() { return nomClient; }
    public void setNomClient(String nomClient) { this.nomClient = nomClient; }

    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }

    public String getNomPrestataire() { return nomPrestataire; }
    public void setNomPrestataire(String nomPrestataire) { this.nomPrestataire = nomPrestataire; }

    public String getMatierePrestataire() { return matierePrestataire; }  // méthode getter mise à jour
    public void setMatierePrestataire(String matierePrestataire) { this.matierePrestataire = matierePrestataire; }

    public String getTelephonePrestataire() { return telephonePrestataire; }
    public void setTelephonePrestataire(String telephonePrestataire) { this.telephonePrestataire = telephonePrestataire; }

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}