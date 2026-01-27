package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;

public class DeviceRequest {

    private int UserID;
    private int DeviceID;
    private DeviceType DeviceType;
    private CommandType CommandType;
    private String CommandData; //Json String

    //Getters
    public int GetUserID() {
        return UserID;
    }
    public void SetUserID(int UserID) {
        this.UserID = UserID;
    }
    public int GetDeviceID() {
        return DeviceID;
    }
    public void SetDeviceID(int DeviceID) {
        this.DeviceID = DeviceID;
    }
    public DeviceType GetDeviceType() {
        return DeviceType;
    }
    public void SetDeviceType(DeviceType DeviceType) {
        this.DeviceType = DeviceType;
    }
    public CommandType GetCommandType() {
        return CommandType;
    }
    public void SetCommandType(CommandType CommandType) {
        this.CommandType = CommandType;
    }
    public String GetCommandData() {
        return CommandData;
    }
    public void SetCommandData(String CommandData) {
        this.CommandData = CommandData;
    }
}
