package application;

public class Prestataire {
    int id;
    int metier_id;
    String nom;
    String metier;
    String prenom;
    String email;
    int ville_id;
    String ville;
    String telephone;
    String description;
    String disponibilite;
    double prix_min;
    double prix_max;

    

        // Constructeur
        public Prestataire(int id, String metier, String nom, String prenom, String email, String ville, String telephone, String description, String disponibilite, double prix_min, double prix_max) {
            this.id = id;
            this.metier = metier;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.ville = ville;
            this.telephone = telephone;
            this.description = description;
            this.disponibilite = disponibilite;
            this.prix_min = prix_min;
            this.prix_max = prix_max;
        }

       

  

        // Constructeur
        public Prestataire(String nom, String prenom, String email, String telephone, String description, String disponibilite, double prix_min, double prix_max) {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.telephone = telephone;
            this.description = description;
            this.disponibilite = disponibilite;
            this.prix_min = prix_min;
            this.prix_max = prix_max;
        }

      
    

    // Getters et setters
    public int getId() {
        return id;
    }
    public String getMetier() { return metier; }
    public String getVille() { return ville; }
    public void setMetier(String metier ){  this.metier = metier; }
    public void setVille(String ville ){  this.ville = ville; }
    public void setId(int id) {
        this.id = id;
    }

    public int getMetier_id() {
        return metier_id;
    }

    public void setMetier_id(int metier_id) {
        this.metier_id = metier_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVille_id() {
        return ville_id;
    }

    public void setVille_id(int ville_id) {
        this.ville_id = ville_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

    public double getPrix_min() {
        return prix_min;
    }

    public void setPrix_min(double prix_min) {
        this.prix_min = prix_min;
    }

    public double getPrix_max() {
        return prix_max;
    }

    public void setPrix_max(double prix_max) {
        this.prix_max = prix_max;
    }
}
