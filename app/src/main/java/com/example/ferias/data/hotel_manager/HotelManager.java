package com.example.ferias.data.hotel_manager;

import com.example.ferias.data.common.Address;
import com.example.ferias.data.common.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HotelManager extends User implements Serializable {

    ////////////////     HOTELS     ////////////////
    private List<String> hotels;

    public HotelManager(){
        this.hotels = new ArrayList<>();
    }

    public HotelManager(String name, String surname, String phone, String email, boolean isGoogle) {
        super(name, surname, phone, email, isGoogle);
        this.hotels = new ArrayList<>();
    }

    public HotelManager(String name, String surname, String email, String phone, String password) {
        super(name, surname, email, phone, password);
        this.hotels = new ArrayList<>();
    }

    //////////////// GETS BEGIN ////////////////
    public List<String> getHotels() {
        return hotels;
    }
    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setHotels(List<String> hotels) {
        this.hotels = hotels;
    }

    public void addHotel(String hotel) {
        if(hotels.isEmpty()){
            hotels = new ArrayList<>();
        }
        this.hotels.add(hotel);
    }

    public void removeHotelbyIndex(int index) {
        this.hotels.remove(index);
    }

    public void removeHotelbyObject(String hotel) {
        int index = 0;
        for(String id: hotels){
            if(id == hotel){
                hotels.remove(index);
                break;
            }
            index++;
        }
    }
    //////////////// SETS END ////////////////

}
