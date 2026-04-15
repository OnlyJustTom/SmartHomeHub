package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.TriggerCondition;

public class TriggerControlRequest {

    private Integer sensorDeviceId;
    private TriggerCondition triggerCondition;

    public Integer getSensorDeviceId() {
        return sensorDeviceId;
    }

    public TriggerCondition getTriggerCondition() {
        return triggerCondition;
    }


}
