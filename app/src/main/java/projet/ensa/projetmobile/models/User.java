package projet.ensa.projetmobile.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("nom")
    private String nom;
    @SerializedName("prenom")
    private String prenom;
    @SerializedName("email")
    private String email;
    @SerializedName("telephone")
    private String telephone;
    @SerializedName("imei")
    private String imei;
    @SerializedName("date_naissance")
    private String date_naissance;
    @SerializedName("sexe")
    private String sexe;
    @SerializedName("isActive")
    private boolean isActive;

    public User(String nom, String prenom, String email, String telephone, String imei, String date_naissance, String sexe, boolean isActive) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.imei = imei;
        this.date_naissance = date_naissance;
        this.sexe = sexe;
        this.isActive = isActive;
    }

    public User(String nom, String telephone) {
        this.nom = nom;
        this.telephone = telephone;
    }
    public User( String telephone) {

        this.telephone = telephone;
    }



    public User(String nom, String prenom, String imei) {
        this.nom = nom;
        this.prenom = prenom;
        this.imei = imei;
    }

    public User(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDate_naissance() {
        return date_naissance;
    }

    public void setDate_naissance(String date_naissance) {
        this.date_naissance = date_naissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", imei='" + imei + '\'' +
                ", date_naissance='" + date_naissance + '\'' +
                ", sexe='" + sexe + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
