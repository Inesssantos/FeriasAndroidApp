package com.example.ferias.data.hotel_manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HotelFeature  implements Serializable {

    private Map<String, Boolean> values;

    public HotelFeature() {
        values = new HashMap<>();
    }

    public HotelFeature(String[] features_name) {
        values = new HashMap<>();

        for(String name : features_name) {
            values.put(name,false);
        }

    }

    //////////////// GETS BEGIN ////////////////
    public int getHotelFeature(){ return values.size();}

    public Map<String, Boolean> getFeatures() {
        return values;
    }

    public boolean getFeatures_Value(String key) {
        return values.get(key);
    }

    //////////////// GETS END ////////////////

    //////////////// SETS BEGIN ////////////////
    public void setFeatures(Map<String, Boolean> features) {
        this.values = features;
    }

    public void setFeatures_Value(String key, boolean value) {
        values.put(key, value);
    }
    //////////////// SETS END ////////////////


    @Override
    public String toString() {
        String data = "HotelFeature{" + '\n';
        for (Map.Entry<String, Boolean> entry : values.entrySet()) {
            data += "{" + entry.getKey() + " : " + entry.getValue() + "}" + '\n';
        }
        data += '}';

        return data;
    }

}