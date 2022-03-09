package com.example.ferias.data.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MyCoordinates implements Serializable {
    private double latitude;
    private double longitude;

    public MyCoordinates(){
    }

    public MyCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLocation() {
        return new LatLng(latitude,longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }
}
