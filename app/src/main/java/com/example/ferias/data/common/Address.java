package com.example.ferias.data.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Address implements Serializable {

    private String country;
    private String city;
    private String address;
    private String zipcode;
    private MyCoordinates coordinates;

    public Address(){
    }

    public Address(String country, String city, String address, String zipcode) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
    }

    public Address(String country, String city, String address, String zipcode, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;

        this.coordinates = new MyCoordinates(latitude,longitude);

    }

    public Address(String country, String city, String address, String zipcode, MyCoordinates coordinates) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zipcode = zipcode;
        this.coordinates = coordinates;
    }

    //////////////// GETS BEGIN ////////////////
    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public LatLng getCoordinates() {
        if(coordinates == null){
            return null;
        }else{
            return coordinates.getLocation();
        }

    }
    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setCoordinates(double latitude, double longitude) {
        coordinates = new MyCoordinates(latitude,longitude);
    }

    //////////////// SETS END ////////////////
}
