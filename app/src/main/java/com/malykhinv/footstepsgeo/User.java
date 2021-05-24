package com.malykhinv.footstepsgeo;

import java.util.Date;

public class User {

    public String name;
    public String personalCode;
    public int imageNumber;
    public Long latitude;
    public Long longtitude;
    public String phoneNumber;
    public Date lastLocationTime;
    public int batteryLevel;

    public User(String name, String personalCode, int imageNumber, Long latitude, Long longtitude, String phoneNumber, Date lastLocationTime, int batteryLevel) {
        this.name = name;
        this.personalCode = personalCode;
        this.imageNumber = imageNumber;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }

    public User() {

    }
}
