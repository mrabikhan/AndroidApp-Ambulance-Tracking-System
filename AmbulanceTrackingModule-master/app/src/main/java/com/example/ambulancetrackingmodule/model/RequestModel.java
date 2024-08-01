package com.example.ambulancetrackingmodule.model;

public class RequestModel {
    String name, email, time;
    double lat, longi;

    RequestModel() {
    }

    public RequestModel(String name, String email, String time, double lat, double longi) {
        this.name = name;
        this.email = email;
        this.time = time;
        this.lat = lat;
        this.longi = longi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
