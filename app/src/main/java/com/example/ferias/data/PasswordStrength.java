package com.example.ferias.data;

import android.graphics.Color;

import com.example.ferias.R;

public enum  PasswordStrength {
    // we use some color in green tint =>
    //more secure is the password, more darker is the color associated
    WEAK(R.string.weak, Color.parseColor("#ff0000"),1),
    MEDIUM(R.string.medium, Color.parseColor("#ff9933"),2),
    STRONG(R.string.strong, Color.parseColor("#99ff66"),3),
    VERY_STRONG(R.string.very_strong, Color.parseColor("#33cc33"),4);

    private final int msg;
    private final int color;
    private final int strength;
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 15;

    PasswordStrength(int msg, int color, int strength) {
        this.msg = msg;
        this.color = color;
        this.strength = strength;
    }

    public int getMsg() {
        return msg;
    }

    public int getColor() {
        return color;
    }

    public int getStrength() {
        return strength;
    }

    public static PasswordStrength calculate(String password) {
        int score = 0;
        // boolean indicating if password has an upper case
        boolean upper = false;
        // boolean indicating if password has a lower case
        boolean lower = false;
        // boolean indicating if password has at least one digit
        boolean digit = false;
        // boolean indicating if password has a leat one special char
        boolean specialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (!specialChar  &&  !Character.isLetterOrDigit(c)) {
                score++;
                specialChar = true;
            } else {
                if (!digit  &&  Character.isDigit(c)) {
                    score++;
                    digit = true;
                } else {
                    if (!upper || !lower) {
                        if (Character.isUpperCase(c)) {
                            upper = true;
                        } else {
                            lower = true;
                        }

                        if (upper && lower) {
                            score++;
                        }
                    }
                }
            }
        }

        int length = password.length();

        if (length > MAX_LENGTH) {
            score++;
        } else if (length < MIN_LENGTH) {
            score = 0;
        }

        // return enum following the score
        switch(score) {
            case 0 : return WEAK;
            case 1 : return MEDIUM;
            case 2 : return STRONG;
            case 3 : return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }
}
