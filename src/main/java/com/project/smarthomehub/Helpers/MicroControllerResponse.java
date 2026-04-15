package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.DeviceType;

public class MicroControllerResponse {

    private String name;
    private String IPAddress;
    private DeviceType deviceType;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIPAddress() {
        return IPAddress;
    }
    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
