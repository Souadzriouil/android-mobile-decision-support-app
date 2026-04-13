package com.example.alomrane;

public class Appartement {
    private String idAppartement;
    private String nbChambres;
    private String nbEtages;
    private String superficie;
    private boolean balcon;
    private boolean cuisine;
    private boolean parking;

    public Appartement(String idAppartement, String nbChambres, String nbEtages, String superficie, boolean balcon, boolean cuisine, boolean parking) {
        this.idAppartement = idAppartement;
        this.nbChambres = nbChambres;
        this.nbEtages = nbEtages;
        this.superficie = superficie;
        this.balcon = balcon;
        this.cuisine = cuisine;
        this.parking = parking;
    }


    // Getters and setters omitted for brevity

    public String getIdAppartement() {
        return idAppartement;
    }

    public void setIdAppartement(String idAppartement) {
        this.idAppartement = idAppartement;
    }

    public String getNbChambres() {
        return nbChambres;
    }

    public void setNbChambres(String nbChambres) {
        this.nbChambres = nbChambres;
    }

    public String getNbEtages() {
        return nbEtages;
    }

    public void setNbEtages(String nbEtages) {
        this.nbEtages = nbEtages;
    }

    public String getSuperficie() {
        return superficie;
    }

    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }

    public boolean isBalcon() {
        return balcon;
    }

    public void setBalcon(boolean balcon) {
        this.balcon = balcon;
    }

    public boolean isCuisine() {
        return cuisine;
    }

    public void setCuisine(boolean cuisine) {
        this.cuisine = cuisine;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }
}
