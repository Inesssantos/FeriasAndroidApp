package com.example.ferias.data.hotel_manager;

import com.example.ferias.data.common.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hotel implements Serializable {

    ////////////////   DATA    ////////////////
    private String manager;
    //////////////////////////////
    private String name;
    private String phone;
    //////////////////////////////
    private Address address;
    //////////////////////////////
    private int rate;
    private float stars;
    //////////////////////////////
    private int total_rooms;
    private int rooms_occupied;
    private float price;
    //////////////////////////////
    private String description;
    //////////////////////////////
    private HotelMoods moods;
    //////////////////////////////
    private HotelFeature feature;
    //////////////////////////////
    private String coverPhoto;
    private List<String> otherPhotos;

    private List<String> bookings;

    public Hotel(){
        ////////////////   DATA    ////////////////
        manager = "";
        //////////////////////////////
        name = "";
        phone = "";
        //////////////////////////////
        address = new Address();
        //////////////////////////////
        rate = 0;
        stars = 0;
        //////////////////////////////
        total_rooms = 0;
        rooms_occupied = 0;
        price = 0;
        //////////////////////////////
        description = "";
        //////////////////////////////
        moods = new HotelMoods();
        //////////////////////////////
        feature = new HotelFeature();
        //////////////////////////////
        coverPhoto = "";
        otherPhotos = new ArrayList<>();
        bookings = new ArrayList<>();
    }

    public Hotel(String name, String phone, String description, Address address, String manager, float price, float stars, int total_rooms, HotelMoods moods, HotelFeature feature) {

        this.name = name;
        this.phone = phone;
        this.description = description;
        this.address = address;
        this.manager = manager;
        this.price = price;
        this.rate = 0;
        this.stars = stars;
        this.total_rooms = total_rooms;
        this.rooms_occupied = 0;
        this.moods = moods;
        this.feature = feature;
        this.coverPhoto = "";
        this.otherPhotos = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }


    //////////////// GETS BEGIN ////////////////
    public String getManager() {
        return manager;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    public int getRate() {
        return rate;
    }

    public float getStars() {
        return stars;
    }

    public int getTotal_Rooms() {
        return total_rooms;
    }

    public int getRooms_Occupied() {
        return rooms_occupied;
    }

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public HotelMoods getMoods() {
        return moods;
    }

    public HotelFeature getFeature() {
        return feature;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public List<String> getOtherPhotos() {
        return otherPhotos;
    }

    public List<String> getBookings() {
        return bookings;
    }
    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setManager(String manager) {
        this.manager = manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public void setTotal_Rooms(int total_rooms) {
        this.total_rooms = total_rooms;
    }

    public void setRooms_Occupied(int rooms_occupied) {
        this.rooms_occupied = rooms_occupied;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMoods(HotelMoods moods) {
        this.moods = moods;
    }

    public void setFeature(HotelFeature feature) {
        this.feature = feature;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public void setOtherPhotos(List<String> otherPhotos) {
        this.otherPhotos = otherPhotos;
    }

    public void setBookings(List<String> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(String booking) {
        if(bookings==null){
            bookings = new ArrayList<>();
        }
        this.bookings.add(booking);
    }

    public void removeBookingbyIndex(int index) {
        this.bookings.remove(index);
    }
    //////////////// SETS END ////////////////



}
