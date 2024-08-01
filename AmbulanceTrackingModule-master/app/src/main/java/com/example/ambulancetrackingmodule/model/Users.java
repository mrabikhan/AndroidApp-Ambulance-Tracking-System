package com.example.ambulancetrackingmodule.model;

public class Users {
    String Uid;
    String name;
    String cnic;
    String email;
    long lat;
    long longi;

    public Users() {
    }

    public Users(String uid, String name, String email, String cnic) {
        Uid = uid;
        this.name = name;
        this.email = email;
        this.cnic = cnic;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLongi() {
        return longi;
    }

    public void setLongi(long longi) {
        this.longi = longi;
    }
}
