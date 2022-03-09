package com.example.ferias.data.traveler;

import java.io.Serializable;
import java.util.Date;

public class Booking implements Serializable {

    ///////////////DATA///////////////
    private String hotelID;
    private String userID;
    private Date enterDate;
    private Date exitDate;
    private int nAdults;
    private int nChildren;
    private float price;
    private boolean expanded= false;

    public Booking(){ }

    public Booking(String hotelID, String userID, Date enterDate, Date exitDate, int nAdults, int nChildren, float price){
        this.hotelID = hotelID;
        this.userID = userID;
        this.enterDate = enterDate;
        this.exitDate = exitDate;
        this.nAdults = nAdults;
        this.nChildren = nChildren;
        this.price = price;

    }

    ////////////GETS BEGIN /////////////

    public String getHotelID() {
        return hotelID;
    }

    public String getUserID() {
        return userID;
    }

    public Date getEnterDate() {
        return enterDate;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public int getnAdults() {
        return nAdults;
    }

    public int getnChildren() {
        return nChildren;
    }

    public float getPrice() {
        return price;
    }

    public boolean isExpanded() {
        return expanded;
    }

    ////////// GETS END ////////////////

    //////////// SETS BEGIN ///////////

    public void setHotelID(String hotelID) {
        this.hotelID = hotelID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEnterDate(Date enterDate) {
        this.enterDate = enterDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    public void setnAdults(int nAdults){
        this.nAdults = nAdults;
    }

    public void setnChildren(int nChildren){
        this.nChildren = nChildren;
    }

    public void setPrice(float price) { this.price = price; }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /////////// SETS END ///////////////
}
