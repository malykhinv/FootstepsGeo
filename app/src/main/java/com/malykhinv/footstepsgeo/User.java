package com.malykhinv.footstepsgeo;

import com.google.android.gms.maps.model.LatLng;

public class User {

    public String id;
    public String name;
    public String personalCode;
    public int imageNumber;
    public LatLng latLng;
    public String phoneNumber;
    public long lastLocationTime;
    public int batteryLevel;

    public User(String id, String name, String personalCode, int imageNumber, LatLng latLng, String phoneNumber, long lastLocationTime, int batteryLevel) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageNumber = imageNumber;
        this.latLng = latLng;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }

    public User() {

    }
}
