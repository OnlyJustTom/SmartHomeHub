package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.CommandType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class RoutineRequest {
    private String name;
    private LocalTime timeToExecute;
    private List<DayOfWeek> daysToExecute;
    private boolean isEnabled;
    private Integer userId;
    private List<RoutineDeviceRequest> routineDevices;

    public static class RoutineDeviceRequest {
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

    // Getters & setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalTime getTimeToExecute() {
        return timeToExecute;
    }
    public void setTimeToExecute(LocalTime timeToExecute) {
        this.timeToExecute = timeToExecute;
    }
    public List<DayOfWeek> getDaysToExecute() {
        return daysToExecute;
    }
    public void setDaysToExecute(List<DayOfWeek> daysToExecute) {
        this.daysToExecute = daysToExecute;
    }
    public boolean isEnabled() {
        return isEnabled;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public List<RoutineDeviceRequest> getRoutineDevices() {
        return routineDevices;
    }
    public void setRoutineDevices(List<RoutineDeviceRequest> routineDevices) {
        this.routineDevices = routineDevices;
    }
}
