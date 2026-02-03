package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.DeviceType;

public class DeviceRequest {

    private int userId;
    private int deviceId;
    private DeviceType deviceType;
    private CommandType commandType;
    private String commandData;

    //Getters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public CommandType getCommandType() { return commandType; }
    public void setCommandType(CommandType commandType) { this.commandType = commandType; }

    public String getCommandData() { return commandData; }
    public void setCommandData(String commandData) { this.commandData = commandData; }

    @Override
    public String toString() {
        return "DeviceRequest{" +
                "UserID=" + userId +
                ", DeviceID=" + deviceId +
                ", DeviceType=" + deviceType +
                ", CommandType=" + commandType +
                ", CommandData='" + commandData + '\'' +
                '}';
    }
}
