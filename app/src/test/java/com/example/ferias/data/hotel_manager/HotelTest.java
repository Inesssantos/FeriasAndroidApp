package com.example.ferias.data.hotel_manager;

import com.example.ferias.data.common.Address;


import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HotelTest {

    @Test
    public void testSetManager() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setManager("Paris");
        assertEquals("Paris", h.getManager());
    }

    @Test
    public void testSetName() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setName("Paris");
        assertEquals("Paris", h.getName());
    }

    @Test
    public void testSetPhone() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setPhone("10001000");
        assertEquals("10001000", h.getPhone());
    }

    @Test
    public void testSetAddress() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        Address b = new Address("Italy", "Venice", "Calle dei martiri 2", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);

        h.setAddress(b);
        String address1 = b.toString();
        String address2 = a.toString();
        assertEquals(address2, h.getAddress().toString());
    }

    @Test
    public void testSetRate() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setRate(40);
        assertEquals(40, h.getRate());
    }

    @Test
    public void testSetStars() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setStars(4);
        assertEquals(4, h.getStars(),0);
    }

    @Test
    public void testSetTotal_Rooms() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setTotal_Rooms(88);
        assertEquals(88, h.getTotal_Rooms());
    }

    @Test
    public void testSetRooms_Occupied() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setRooms_Occupied(10);
        assertEquals(10, h.getRooms_Occupied());
    }

    @Test
    public void testSetPrice() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setPrice(20f);
        assertEquals(20f, h.getPrice(),0);
    }

    @Test
    public void testSetDescription() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.setDescription("Pretty nasty");
        assertEquals("Pretty nasty", h.getDescription());
    }

    @Test
    public void testMoods() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Map<String, Boolean> moods = new HashMap<>();
        moods.put("Chill", false);
        moods.put("Depressed", true);
        hm.setMoods(moods);
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        assertEquals(true, h.getMoods().getMoods_Values("Depressed"));
    }

    @Test
    public void testFeature() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelMoods hm = new HotelMoods();
        HotelFeature hf = new HotelFeature();
        Map<String, Boolean> features = new HashMap<>();
        features.put("Internet", true);
        features.put("Water", false);
        hf.setFeatures(features);
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        assertEquals(true, h.getFeature().getFeatures_Value("Internet"));
    }

    @Test
    public void testAddBooking() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.addBooking("It's for me");
        List<String> bookings = new ArrayList<>();
        bookings = h.getBookings();
        assertEquals("It's for me", bookings.get(0));
    }

    @Test
    public void testRemoveBookingbyIndex() {
        Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);
        HotelFeature hf = new HotelFeature();
        HotelMoods hm = new HotelMoods();
        Hotel h = new Hotel("Hilton", "0433567890", "Very beautiful", a, "Paul Monet", 30f, 5f, 35, hm, hf);
        h.addBooking("It's for me");
        h.addBooking("It's for you");
        h.removeBookingbyIndex(0);
        List<String> bookings = new ArrayList<>();
        bookings = h.getBookings();
        assertEquals("It's for you", bookings.get(0));
    }

}