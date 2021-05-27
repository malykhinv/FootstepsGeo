package com.malykhinv.footstepsgeo;

import android.location.Location;

public class User {

    public String id;
    public String name;
    public String personalCode;
    public int imageNumber;
    public Location location;
    public String phoneNumber;
    public long lastLocationTime;
    public int batteryLevel;

    public User(String id, String name, String personalCode, int imageNumber, Location location, String phoneNumber, long lastLocationTime, int batteryLevel) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageNumber = imageNumber;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }

    public User() {

    }
}
