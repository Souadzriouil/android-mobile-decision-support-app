package com.example.alomrane;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Marker {

    private double x;
    private double y;
    private String name;
    private double age;
    private double prix;

    public Marker() {
    }

    public Marker(double x, double y, String name, double age, double prix) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.age = age;
        this.prix = prix;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public double getAge() {
        return age;
    }

    public double getPrix() {
        return prix;
    }
}
