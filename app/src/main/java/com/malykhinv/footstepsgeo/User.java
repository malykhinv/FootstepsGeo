package com.malykhinv.footstepsgeo;

public class User {

    public String id;
    public String name;
    public String personalCode;
    public int imageNumber;
    public double latitude;
    public double longitude;
    public String phoneNumber;
    public long lastLocationTime;
    public int batteryLevel;

    public User(String id, String name, String personalCode, int imageNumber, double latitude, double longitude, String phoneNumber, long lastLocationTime, int batteryLevel) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageNumber = imageNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }

    public User() {

    }
}
