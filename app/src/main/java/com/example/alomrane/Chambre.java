package com.example.alomrane;

public class Chambre {
    private int degre;
    private String direction;
    private String idSpinner;
    private String idappartement;
    private String spinner;
    private String userId;

    public Chambre() {}

    public Chambre(int degre, String direction, String idSpinner, String idAppartement, String spinner, String userId) {
        this.degre = degre;
        this.direction = direction;
        this.idSpinner = idSpinner;
        this.idappartement = idAppartement;
        this.spinner = spinner;
        this.userId = userId;
    }

    public int getDegre() {
        return degre;
    }

    public void setDegre(int degre) {
        this.degre = degre;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getIdSpinner() {
        return idSpinner;
    }

    public void setIdSpinner(String idSpinner) {
        this.idSpinner = idSpinner;
    }

    public String getIdAppartement() {
        return idappartement;
    }

    public void setIdAppartement(String idAppartement) {
        this.idappartement = idAppartement;
    }

    public String getSpinner() {
        return spinner;
    }

    public void setSpinner(String spinner) {
        this.spinner = spinner;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
