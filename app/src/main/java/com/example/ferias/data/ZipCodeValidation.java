package com.example.ferias.data;

import java.util.HashMap;

public class ZipCodeValidation {

    private final HashMap<String,String> postalformat;

    public ZipCodeValidation() {
        postalformat = new HashMap<>();

        postalformat.put("AD","^(?:AD)*(\\d{3})$");
        postalformat.put("AE","");
        postalformat.put("AF","");
        postalformat.put("AG","");
        postalformat.put("AI","");
        postalformat.put("AL","^(\\d{4})$");
        postalformat.put("AM","^(\\d{6})$");
        postalformat.put("AO","");
        postalformat.put("AQ","");
        postalformat.put("AR","^[A-Z]?\\d{4}[A-Z]{0,3}$");
        postalformat.put("AS","96799");
        postalformat.put("AT","^(\\d{4})$");
        postalformat.put("AU","^(\\d{4})$");
        postalformat.put("AW","");
        postalformat.put("AX","^(?:FI)*(\\d{5})$");
        postalformat.put("AZ","^(?:AZ)*(\\d{4})$");
        postalformat.put("BA","^(\\d{5})$");
        postalformat.put("BB","^(?:BB)*(\\d{5})$");
        postalformat.put("BD","^(\\d{4})$");
        postalformat.put("BE","^(\\d{4})$");
        postalformat.put("BF","");
        postalformat.put("BG","^(\\d{4})$");
        postalformat.put("BH","^(\\d{3}\\d?)$");
        postalformat.put("BI","");
        postalformat.put("BJ","");
        postalformat.put("BL","^(\\d{5})$");
        postalformat.put("BM","^([A-Z]{2}\\d{2})$");
        postalformat.put("BN","^([A-Z]{2}\\d{4})$");
        postalformat.put("BO","");
        postalformat.put("BQ","");
        postalformat.put("BR","^\\d{5}-\\d{3}$");
        postalformat.put("BS","");
        postalformat.put("BT","");
        postalformat.put("BV","");
        postalformat.put("BW","");
        postalformat.put("BY","^(\\d{6})$");
        postalformat.put("BZ","");
        postalformat.put("CA","^([ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJKLMNPRSTVWXYZ]) ?(\\d[ABCEGHJKLMNPRSTVWXYZ]\\d)$");
        postalformat.put("CC","6799");
        postalformat.put("CD","");
        postalformat.put("CF","");
        postalformat.put("CG","");
        postalformat.put("CH","^(\\d{4})$");
        postalformat.put("CI","");
        postalformat.put("CK","^(\\d{4})$");
        postalformat.put("CL","^(\\d{7})$");
        postalformat.put("CM","");
        postalformat.put("CN","^(\\d{6})$");
        postalformat.put("CO","^(\\d{6})$");
        postalformat.put("CR","^\\d{4,5}|\\d{3}-\\d{4}$");
        postalformat.put("CS","^(\\d{5})$");
        postalformat.put("CU","^(?:CP)*(\\d{5})$");
        postalformat.put("CV","^(\\d{4})$");
        postalformat.put("CW","");
        postalformat.put("CX","^(\\d{4})$");
        postalformat.put("CY","^(\\d{4})$");
        postalformat.put("CZ","^\\d{3}\\s?\\d{2}$");
        postalformat.put("DE","^(\\d{5})$");
        postalformat.put("DJ","");
        postalformat.put("DK","^(\\d{4})$");
        postalformat.put("DM","");
        postalformat.put("DO","^(\\d{5})$");
        postalformat.put("DZ","^(\\d{5})$");
        postalformat.put("EC","^([a-zA-Z]\\d{4}[a-zA-Z])$");
        postalformat.put("EE","^(\\d{5})$");
        postalformat.put("EG","^(\\d{5})$");
        postalformat.put("EH","");
        postalformat.put("ER","");
        postalformat.put("ES","^(\\d{5})$");
        postalformat.put("ET","^(\\d{4})$");
        postalformat.put("FI","^(?:FI)*(\\d{5})$");
        postalformat.put("FJ","");
        postalformat.put("FK","FIQQ 1ZZ");
        postalformat.put("FM","^(9694[1-4])([ \\-]\\d{4})?$");
        postalformat.put("FO","^(?:FO)*(\\d{3})$");
        postalformat.put("FR","^\\d{2}[ ]?\\d{3}$");
        postalformat.put("GA","");
        postalformat.put("GB","^([Gg][Ii][Rr]\\s?0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))\\s?[0-9][A-Za-z]{2})$");
        postalformat.put("GD","");
        postalformat.put("GE","^(\\d{4})$");
        postalformat.put("GF","^((97|98)3\\d{2})$");
        postalformat.put("GG","^((?:(?:[A-PR-UWYZ][A-HK-Y]\\d[ABEHMNPRV-Y0-9]|[A-PR-UWYZ]\\d[A-HJKPS-UW0-9])\\s\\d[ABD-HJLNP-UW-Z]{2})|GIR\\s?0AA)$");
        postalformat.put("GH","");
        postalformat.put("GI","");
        postalformat.put("GL","^(\\d{4})$");
        postalformat.put("GM","");
        postalformat.put("GN","^(\\d{3})$");
        postalformat.put("GP","^((97|98)\\d{3})$");
        postalformat.put("GQ","");
        postalformat.put("GR","^\\d{3}[ ]?\\d{2}$");
        postalformat.put("GS","SIQQ 1ZZ");
        postalformat.put("GT","^(\\d{5})$");
        postalformat.put("GU","^969[123]\\d([ \\-]\\d{4})?$");
        postalformat.put("GW","^(\\d{4})$");
        postalformat.put("GY","");
        postalformat.put("HK","");
        postalformat.put("HM","^(\\d{4})$");
        postalformat.put("HN","^([A-Z]{2}\\d{4})$");
        postalformat.put("HR","^(?:HR)*(\\d{5})$");
        postalformat.put("HT","^(?:HT)*(\\d{4})$");
        postalformat.put("HU","^(\\d{4})$");
        postalformat.put("ID","^(\\d{5})$");
        postalformat.put("IE","^(D6W|[AC-FHKNPRTV-Y][0-9]{2})\\s?([AC-FHKNPRTV-Y0-9]{4})");
        postalformat.put("IL","^(\\d{7}|\\d{5})$");
        postalformat.put("IM","^((?:(?:[A-PR-UWYZ][A-HK-Y]\\d[ABEHMNPRV-Y0-9]|[A-PR-UWYZ]\\d[A-HJKPS-UW0-9])\\s\\d[ABD-HJLNP-UW-Z]{2})|GIR\\s?0AA)$");
        postalformat.put("IN","^(\\d{6})$");
        postalformat.put("IO","BBND 1ZZ");
        postalformat.put("IQ","^(\\d{5})$");
        postalformat.put("IR","^(\\d{10})$");
        postalformat.put("IS","^(\\d{3})$");
        postalformat.put("IT","^(\\d{5})$");
        postalformat.put("JE","^((?:(?:[A-PR-UWYZ][A-HK-Y]\\d[ABEHMNPRV-Y0-9]|[A-PR-UWYZ]\\d[A-HJKPS-UW0-9])\\s\\d[ABD-HJLNP-UW-Z]{2})|GIR\\s?0AA)$");
        postalformat.put("JM","");
        postalformat.put("JO","^(\\d{5})$");
        postalformat.put("JP","^\\d{3}-\\d{4}$");
        postalformat.put("KE","^(\\d{5})$");
        postalformat.put("KG","^(\\d{6})$");
        postalformat.put("KH","^(\\d{5})$");
        postalformat.put("KI","");
        postalformat.put("KM","");
        postalformat.put("KN","");
        postalformat.put("KP","^(\\d{6})$");
        postalformat.put("KR","^\\d{3}[\\-]\\d{3}$");
        postalformat.put("KW","^(\\d{5})$");
        postalformat.put("KY","");
        postalformat.put("KZ","^(\\d{6})$");
        postalformat.put("LA","^(\\d{5})$");
        postalformat.put("LB","^(\\d{4}(\\d{4})?)$");
        postalformat.put("LC","");
        postalformat.put("LI","^(948[5-9])|(949[0-7])$");
        postalformat.put("LK","^(\\d{5})$");
        postalformat.put("LR","^(\\d{4})$");
        postalformat.put("LS","^(\\d{3})$");
        postalformat.put("LT","^(?:LT)*(\\d{5})$");
        postalformat.put("LU","^(?:L-)?\\d{4}$");
        postalformat.put("LV","^(?:LV)*(\\d{4})$");
        postalformat.put("LY","");
        postalformat.put("MA","^(\\d{5})$");
        postalformat.put("MC","^(\\d{5})$");
        postalformat.put("MD","^MD-\\d{4}$");
        postalformat.put("ME","^(\\d{5})$");
        postalformat.put("MF","^(\\d{5})$");
        postalformat.put("MG","^(\\d{3})$");
        postalformat.put("MH","^969\\d{2}(-\\d{4})$");
        postalformat.put("MK","^(\\d{4})$");
        postalformat.put("ML","");
        postalformat.put("MM","^(\\d{5})$");
        postalformat.put("MN","^(\\d{6})$");
        postalformat.put("MO","");
        postalformat.put("MP","^9695[012]([ \\-]\\d{4})?$");
        postalformat.put("MQ","^9[78]2\\d{2}$");
        postalformat.put("MR","");
        postalformat.put("MS","");
        postalformat.put("MT","^[A-Z]{3}\\s?\\d{4}$");
        postalformat.put("MU","(\\d{3}[A-Z]{2}\\d{3})?");
        postalformat.put("MV","^(\\d{5})$");
        postalformat.put("MW","^(\\d{6})$");
        postalformat.put("MX","^(\\d{5})$");
        postalformat.put("MY","^(\\d{5})$");
        postalformat.put("MZ","^(\\d{4})$");
        postalformat.put("NA","");
        postalformat.put("NC","^(\\d{5})$");
        postalformat.put("NE","^(\\d{4})$");
        postalformat.put("NF","^(\\d{4})$");
        postalformat.put("NG","^(\\d{6})$");
        postalformat.put("NI","^((\\d{4}-)?\\d{3}-\\d{3}(-\\d{1})?)?$");
        postalformat.put("NL","^(\\d{4}\\s?[a-zA-Z]{2})$");
        postalformat.put("NO","^(\\d{4})$");
        postalformat.put("NP","^(\\d{5})$");
        postalformat.put("NR","");
        postalformat.put("NU","");
        postalformat.put("NZ","^(\\d{4})$");
        postalformat.put("OM","^(PC )?\\d{3}$");
        postalformat.put("PA","");
        postalformat.put("PE","");
        postalformat.put("PF","^((97|98)7\\d{2})$");
        postalformat.put("PG","^(\\d{3})$");
        postalformat.put("PH","^(\\d{4})$");
        postalformat.put("PK","^(\\d{5})$");
        postalformat.put("PL","^\\d{2}-\\d{3}$");
        postalformat.put("PM","^9[78]5\\d{2}$");
        postalformat.put("PN","\"PCRN 1ZZ\"");
        postalformat.put("PR","^00[679]\\d{2}(?:-\\d{4})?$");
        postalformat.put("PS","");
        postalformat.put("PT","^\\d{4}([\\-]\\d{3})?$");
        postalformat.put("PW","^(96940)$");
        postalformat.put("PY","^(\\d{4})$");
        postalformat.put("QA","");
        postalformat.put("RE","^((97|98)(4|7|8)\\d{2})$");
        postalformat.put("RO","^(\\d{6})$");
        postalformat.put("RS","^(\\d{6})$");
        postalformat.put("RU","^(\\d{6})$");
        postalformat.put("RW","");
        postalformat.put("SA","^(\\d{5})$");
        postalformat.put("SB","");
        postalformat.put("SC","");
        postalformat.put("SD","^(\\d{5})$");
        postalformat.put("SS","");
        postalformat.put("SE","^(?:SE)?\\d{3}\\s\\d{2}$");
        postalformat.put("SG","^(\\d{6})$");
        postalformat.put("SH","^(STHL1ZZ)$");
        postalformat.put("SI","^(?:SI)*(\\d{4})$");
        postalformat.put("SJ","^(\\d{4})$");
        postalformat.put("SK","^\\d{3}\\s?\\d{2}$");
        postalformat.put("SL","");
        postalformat.put("SM","^(4789\\d)$");
        postalformat.put("SN","^(\\d{5})$");
        postalformat.put("SO","^([A-Z]{2}\\d{5})$");
        postalformat.put("SR","");
        postalformat.put("ST","");
        postalformat.put("SV","^(?:CP)*(\\d{4})$");
        postalformat.put("SX","");
        postalformat.put("SY","");
        postalformat.put("SZ","^([A-Z]\\d{3})$");
        postalformat.put("TC","^(TKCA 1ZZ)$");
        postalformat.put("TD","");
        postalformat.put("TF","");
        postalformat.put("TG","");
        postalformat.put("TH","^(\\d{5})$");
        postalformat.put("TJ","^(\\d{6})$");
        postalformat.put("TK","");
        postalformat.put("TL","");
        postalformat.put("TM","^(\\d{6})$");
        postalformat.put("TN","^(\\d{4})$");
        postalformat.put("TO","");
        postalformat.put("TR","^(\\d{5})$");
        postalformat.put("TT","");
        postalformat.put("TV","");
        postalformat.put("TW","^\\d{3}(\\d{2})?$");
        postalformat.put("TZ","");
        postalformat.put("UA","^(\\d{5})$");
        postalformat.put("UG","");
        postalformat.put("UM","");
        postalformat.put("US","^\\d{5}([ \\-]\\d{4})?$");
        postalformat.put("UY","^(\\d{5})$");
        postalformat.put("UZ","^(\\d{6})$");
        postalformat.put("VA","^(\\d{5})$");
        postalformat.put("VC","");
        postalformat.put("VE","^(\\d{4})$");
        postalformat.put("VG","");
        postalformat.put("VI","^008(([0-4]\\d)|(5[01]))([ \\-]\\d{4})?$");
        postalformat.put("VN","^(\\d{6})$");
        postalformat.put("VU","");
        postalformat.put("WF","^(986\\d{2})$");
        postalformat.put("WS","");
        postalformat.put("XK","^\\d{5}$");
        postalformat.put("YE","");
        postalformat.put("YT","^(976\\d{2})$");
        postalformat.put("YU","^\\d{5}$");
        postalformat.put("ZA","^\\d{4}$");
        postalformat.put("ZM","^\\d{5}$");
        postalformat.put("ZW","");
        postalformat.put("CS","^(\\d{5})$");
    }

    public boolean validation_code(String country, String zipcode){
        String matches = postalformat.get(country);

        if(!matches.isEmpty()){
            return zipcode.matches(matches);
        }

        return true;
    }

    /*

    public HashMap<String, String> getPostalformat() {
        return postalformat;
    }

    public void setPostalformat(HashMap<String, String> postalformat) {
        this.postalformat = postalformat;
    }

    public void insertPostalformat(String key, String value){
        this.postalformat.put(key, value);
    }

     */
}
