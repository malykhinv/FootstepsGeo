package com.malykhinv.footstepsgeo;

import java.util.ArrayList;

public class User {

    public String id;
    public String name;
    public String personalCode;
    public String imageUrl;
    public int imageNumber;
    public ArrayList<Double> position;
    public String phoneNumber;
    public long lastLocationTime;
    public int batteryLevel;
    public ArrayList<String> friendsIds;

    public User(String id, String name, String personalCode, String imageUrl, int imageNumber, ArrayList<Double> position, String phoneNumber, long lastLocationTime, int batteryLevel, ArrayList<String> friendsIds) {
        this.id = id;
        this.name = name;
        this.personalCode = personalCode;
        this.imageUrl = imageUrl;
        this.imageNumber = imageNumber;
        this.position = position;
        this.phoneNumber = phoneNumber;
        this.lastLocationTime = lastLocationTime;
        this.batteryLevel = batteryLevel;
        this.friendsIds = friendsIds;
    }

    public User() {

    }
}
