package com.example.ferias.data.hotel_manager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HotelManagerTest {
    HotelManager hotelManager = new HotelManager("Pippo", "Baudo", "33333456789", "pippobaudo@rai.it", true);

    @Test
    public void testAddHotel() {
        hotelManager.addHotel("Hilton");
        List<String> hotels = new ArrayList<>();
        hotels = hotelManager.getHotels();
        assertEquals("Hilton", hotels.get(0));
    }

    @Test
    public void testRemoveHotelbyIndex() {
        hotelManager.addHotel("Hilton");
        hotelManager.addHotel("Paris");
        hotelManager.removeHotelbyIndex(0);
        List<String> hotels = new ArrayList<>();
        hotels = hotelManager.getHotels();
        assertEquals("Paris", hotels.get(0));
    }

    @Test
    public void testRemoveHotelbyObject() {
        hotelManager.addHotel("Hilton");
        hotelManager.addHotel("Paris");
        hotelManager.removeHotelbyObject("Hilton");
        List<String> hotels = new ArrayList<>();
        hotels = hotelManager.getHotels();
        assertEquals("Paris", hotels.get(0));
    }

}