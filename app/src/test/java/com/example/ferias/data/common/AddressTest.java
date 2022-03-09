package com.example.ferias.data.common;

import com.google.android.gms.maps.model.LatLng;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    Address address = new Address("Portugal", "Coimbra", "Coimbra", "3040-222");
    Address a = new Address("Italy", "Venice", "Calle dei martiri 1", "31032", 40, 40);

    @Test
    void getCountry() {
        //shoud fail
        //assertEquals("Coimbra", address.getCountry());

        //shoud pass
        assertEquals("Portugal", address.getCountry());
    }

    @Test
    void setCity() {
        address.setCity("ko");
    }

    @Test
    void getCity() {
        //shoud not pass
        //assertEquals("Coimbra", address.getCity());

        //shoud pass
        address.setCity("ko");
        assertEquals("ko", address.getCity());
    }

    @Test
    public void testGetCountry() {
        assertEquals("Italy", a.getCountry());
    }

    @Test
    public void testGetCity() {
        assertEquals("Venice", a.getCity());
    }

    @Test
    public void testGetAddress() {
        assertEquals("Calle dei martiri 1", a.getAddress());
    }

    @Test
    public void testGetZipcode() {
        assertEquals("31032", a.getZipcode());
    }

    @Test
    public void testGetCoordinates() {
        LatLng l1 = new LatLng(40, 40);
        LatLng l2 = a.getCoordinates();
        assertEquals(l2, l1);
    }

    @Test
    public void testSetCountry() {
        a.setCountry("Namibia");
        assertEquals("Namibia", a.getCountry());
    }

    @Test
    public void testSetCity() {
        a.setCity("Treviso");
        assertEquals("Treviso", a.getCity());
    }

    @Test
    public void testSetAddress() {
        a.setAddress("Calle dei sospiri 33");
        assertEquals("Calle dei sospiri 33", a.getAddress());
    }

    @Test
    public void testSetZipcode() {
        a.setZipcode("33066");
        assertEquals("33066", a.getZipcode());
    }

    @Test
    public void testSetCoordinates() {
        a.setCoordinates(20, 60);
        LatLng l1 = new LatLng(20, 60);
        LatLng l2 = a.getCoordinates();
        assertEquals(l2, l1);
    }
}