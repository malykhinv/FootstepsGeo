package com.malykhinv.footstepsgeo;

import java.util.Date;

public class User {

    public final String name;
    public final String personalCode;
    public final int imageNumber;
    public final Long latitude;
    public final Long longtitude;
    public final String phoneNumber;
    public final Date lastLocationTime;
    public final int batteryLevel;

    public User(String name, String personalCode, int imageNumber, Long latitude, Long longtitude, String phoneNumber, Date lastLocationTime, int batteryLevel){
        this.name = name;
        this.personalCode = personalCode;
        this.imageNumber = imageNumber;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }
}
