package com.example.ferias.data.common;

import com.google.type.LatLng;

import org.jetbrains.annotations.Contract;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyCoordinatesTest {
    MyCoordinates myCoordinates = new MyCoordinates(40, 89);

    @Test
    void TestingGets()
    {
        assertEquals(Double.valueOf(40), myCoordinates.getLatitude());
        assertEquals(Double.valueOf(89), myCoordinates.getLongitude());
    }

    @Test
    void TestingSets()
    {
        myCoordinates.setLatitude(20);
        assertEquals(Double.valueOf(20), myCoordinates.getLatitude());

        myCoordinates.setLongitude(23);
        assertEquals(Double.valueOf(20), myCoordinates.getLongitude());
    }

    @Test
    public void testSetLatitude() {
        MyCoordinates myc = new MyCoordinates(40,40);
        myc.setLatitude(30);
        assertEquals(30, myc.getLatitude());
    }

    @Test
    public void testSetLongitude() {
        MyCoordinates myc = new MyCoordinates(40,40);
        myc.setLongitude(30);
        assertEquals(30, myc.getLongitude());
    }

}