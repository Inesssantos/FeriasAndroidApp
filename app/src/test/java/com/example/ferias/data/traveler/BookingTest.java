package com.example.ferias.data.traveler;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class BookingTest {
    @Test
    public void testIsExpanded() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        assertEquals(false, booking.isExpanded());
    }

    @Test
    public void testSetHotelID() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setHotelID("non");
        assertEquals("non", booking.getHotelID());
    }

    @Test
    public void testSetUserID() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setUserID("non");
        assertEquals("non", booking.getUserID());
    }

    @Test
    public void testSetEnterDate() {
        Date date = new Date(12062004);
        Date date2 = new Date(11062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setEnterDate(date2);
        assertEquals(date2.toString(), booking.getEnterDate().toString());
    }

    @Test
    public void testSetExitDate() {
        Date date = new Date(12062004);
        Date date2 = new Date(11062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setExitDate(date2);
        assertEquals(date2.toString(), booking.getExitDate().toString());
    }

    @Test
    public void testSetnAdults() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setnAdults(12);
        assertEquals(12, booking.getnAdults());
    }

    @Test
    public void testSetnChildren() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setnChildren(12);
        assertEquals(12, booking.getnChildren());
    }

    @Test
    public void testSetPrice() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setPrice(12f);
        assertEquals(12f, booking.getPrice(),0);
    }

    @Test
    public void testSetExpanded() {
        Date date = new Date(12062004);
        Booking booking = new Booking("sus", "sas", date,date , 4, 5, 39f);
        booking.setExpanded(true);
        assertEquals(true, booking.isExpanded());
    }
}