package com.example.ferias.data;

public class GenerateUniqueIds {

    public static String generateId(){
        // Alphanumeric characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String autoId = "";

        for (int i = 0; i < 28; i++) {
            autoId += chars.charAt((int) Math.floor(Math.random() * chars.length()));
        }

        return autoId;
    }

}
