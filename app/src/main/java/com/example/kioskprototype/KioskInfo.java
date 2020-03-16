package com.example.kioskprototype;

public class KioskInfo {
    private String name;
    private int id;
    private KioskInfo(){
        id = 1;
        name = "Kiosk station leuven";
    }
    private static KioskInfo mKioskInfo;
    public static KioskInfo get(){
        if(mKioskInfo == null){
            mKioskInfo = new KioskInfo();
        }
        return mKioskInfo;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
}
