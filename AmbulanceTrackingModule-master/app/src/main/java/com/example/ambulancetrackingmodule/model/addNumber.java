package com.example.ambulancetrackingmodule.model;

public class addNumber {
    String number;
    String Uid;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public addNumber(String number, String Uid) {
        this.number = number;
        this.Uid = Uid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
