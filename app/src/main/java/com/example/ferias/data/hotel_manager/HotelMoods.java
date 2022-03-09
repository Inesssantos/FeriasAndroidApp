package com.example.ferias.data.hotel_manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HotelMoods implements Serializable {

    private Map<String, Boolean> values;
    private Map<String, String> moods_icons;

    public HotelMoods() {
        values = new HashMap<>();
        moods_icons = new HashMap<>();
    }

    public HotelMoods(String[] names, String[] icons) {
        values = new HashMap<>();
        moods_icons = new HashMap<>();

        if(names.length == icons.length){
            for(String name : names) {
                values.put(name,false);
            }

            int i = 0;
            for(String icon : icons) {

                moods_icons.put(names[i],icon);
                i++;
            }
        }

        this.toString();
    }

    //////////////// GETS BEGIN ////////////////
    public Map<String, Boolean> getMoods() {
        return values;
    }

    public Map<String, String> getMoods_Icons() {
        return moods_icons;
    }

    public String getMoods_Icon(String key) {
        return moods_icons.get(key);
    }

    public boolean getMoods_Values(String key) {
        return values.get(key);
    }

    public boolean getMoods_Verification(){
        boolean select = false;
        for (Map.Entry<String, Boolean> entry : values.entrySet()){
            if(entry.getValue()){
               select = true;
            }
        }
        return select;
    }
    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setMoods(Map<String, Boolean> moods) {
        this.values = moods;
    }

    public void setMoods_Icons(Map<String, String> moods_icons) {
        this.moods_icons = moods_icons;
    }

    public void setMoods_Value(String key, boolean value) {
        values.put(key, value);
    }
    //////////////// SETS END ////////////////


    @Override
    public String toString() {
        String data = "Hotel Moods{" + '\n';
        for (Map.Entry<String, Boolean> entry : values.entrySet()) {
            data += "{" + entry.getKey() + " : " + entry.getValue() + "}" + '\n';
        }
        data += '}';

        return data;
    }

}
