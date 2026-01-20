package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.Domain.Device;
import com.project.smarthomehub.Domain.User;

public class Link {

    int userID;
    int deviceID;

    //Getters

    public Link(int userID, int deviceID) {
        this.userID = userID;
        this.deviceID = deviceID;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public int getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
}
