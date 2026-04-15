package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.TriggerCondition;

import java.util.List;

public class TriggerRequest {

    private Integer triggerDevice;
    private TriggerCondition triggerCondition;

    private List<TriggerDeviceRequst> targetDevices;

    public static class TriggerDeviceRequst {
        private Integer deviceId;
        private CommandType commandType;
        private String commandData;

        public Integer getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Integer deviceId) {
            this.deviceId = deviceId;
        }

        public CommandType getCommandType() {
            return commandType;
        }

        public void setCommandType(CommandType commandType) {
            this.commandType = commandType;
        }

        public String getCommandData() {
            return commandData;
        }

        public void setCommandData(String commandData) {
            this.commandData = commandData;
        }
    }

    public Integer getTriggerDevice() {
        return triggerDevice;
    }
    public void setTriggerDevice(Integer triggerDevice) {
        this.triggerDevice = triggerDevice;
    }

    public TriggerCondition getTriggerCondition() {
        return triggerCondition;
    }
    public void setTriggerCondition(TriggerCondition triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public List<TriggerDeviceRequst> getTargetDevices() {
        return targetDevices;
    }
    public void setTargetDevices(List<TriggerDeviceRequst> targetDevices) {
        this.targetDevices = targetDevices;
    }
}
