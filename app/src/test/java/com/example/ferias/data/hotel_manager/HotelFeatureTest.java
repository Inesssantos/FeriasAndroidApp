package com.example.ferias.data.hotel_manager;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HotelFeatureTest {

    @Test
    public void testGetFeatures_Value() {
        HotelFeature hotelFeature = new HotelFeature();
        Map<String, Boolean> features = new HashMap<>();
        Map<String, Boolean> gimme = new HashMap<>();

        features.put("Internet", true);
        features.put("Water", false);
        hotelFeature.setFeatures(features);
        assertEquals(false, hotelFeature.getFeatures_Value("Water"));
    }

    @Test
    public void testGetFeatures() {
        HotelFeature hotelFeature = new HotelFeature();
        Map<String, Boolean> features = new HashMap<>();
        Map<String, Boolean> gimme = new HashMap<>();

        features.put("Internet", true);
        features.put("Water", false);
        hotelFeature.setFeatures(features);
        gimme = hotelFeature.getFeatures();
        assertEquals(false, gimme.get("Water"));
    }

    @Test
    public void testSetFeatures_Value() {
        HotelFeature hotelFeature = new HotelFeature();
        Map<String, Boolean> features = new HashMap<>();
        Map<String, Boolean> gimme = new HashMap<>();

        features.put("Internet", true);
        features.put("Water", false);
        hotelFeature.setFeatures(features);
        hotelFeature.setFeatures_Value("Electricity", false);
        assertEquals(false, hotelFeature.getFeatures_Value("Electricity"));
    }
}