package com.malykhinv.footstepsgeo;

import java.util.ArrayList;

public class User {

    public String id;
    public String name;
    public String personalCode;
    public String imageUrl;
    public ArrayList<Double> position;
    public long lastLocationTime;
    public int batteryLevel;
    public ArrayList<String> friendsIds;

    public User(String id, String name, String personalCode, String imageUrl, ArrayList<Double> position, long lastLocationTime, int batteryLevel, ArrayList<String> friendsIds) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageUrl = imageUrl;
        this.position = position;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
        this.friendsIds = friendsIds;
    }

    public User(String id, String name, String personalCode, String imageUrl, long lastLocationTime, int batteryLevel) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageUrl = imageUrl;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
    }

    public User() {

    }

}
