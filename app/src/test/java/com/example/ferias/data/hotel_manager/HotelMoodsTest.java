package com.example.ferias.data.hotel_manager;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HotelMoodsTest {
    @Test
    public void testGetMoods_Values() {
        HotelMoods hotelMoods = new HotelMoods();
        Map<String, Boolean> moods = new HashMap<>();
        moods.put("Chill", false);
        moods.put("Depressed", true);
        hotelMoods.setMoods(moods);
        assertEquals(true, hotelMoods.getMoods_Values("Depressed"));
    }

    @Test
    public void testSetMoods_Value() {
        HotelMoods hotelMoods = new HotelMoods();
        hotelMoods.setMoods_Value("Happy", true);
        assertEquals(true, hotelMoods.getMoods_Values("Happy"));
    }

}