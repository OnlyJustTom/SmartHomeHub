package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.TriggerCondition;

public class TriggerControlRequest {

    private String deviceIPAddress;
    private TriggerCondition triggerCondition;

    public String getDeviceIPAddress() {
        return deviceIPAddress;
    }

    public void setDeviceIPAddress(String deviceIPAddress) {
        this.deviceIPAddress = deviceIPAddress;
    }

    public TriggerCondition getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(TriggerCondition triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

}
