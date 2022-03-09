package com.example.ferias.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PasswordStrengthTest {

    @Test
    public void tetsCalculate() {
        String password= "ciao";
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        assertEquals(1, passwordStrength.getStrength());
    }
    @Test
    public void tetsCalculate2() {
        String password= "ciaobello";
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        assertEquals(1, passwordStrength.getStrength());
    }

    @Test
    public void tetsCalculate3() {
        String password= "Ciaobello";
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        assertEquals(2, passwordStrength.getStrength());
    }

    @Test
    public void tetsCalculate4() {
        String password= "Ciaobello4";
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        assertEquals(1, passwordStrength.getStrength());
    }

    @Test
    public void tetsCalculate5() {
        String password= "Ciaobello5!";
        PasswordStrength passwordStrength = PasswordStrength.calculate(password);
        assertEquals(1, passwordStrength.getStrength());
    }
}