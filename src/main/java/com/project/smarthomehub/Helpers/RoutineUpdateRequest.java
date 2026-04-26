package com.project.smarthomehub.Helpers;

import com.project.smarthomehub.CommandType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class RoutineUpdateRequest {

    private Integer id;
    private String name;
    private LocalTime timeToExecute;
    private List<DayOfWeek> daysToExecute;
    private boolean isEnabled;
    private Integer userId;
    private List<RoutineUpdateDeviceRequest> routineDevices;

    public static class RoutineUpdateDeviceRequest {
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
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
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
    public List<RoutineUpdateDeviceRequest> getRoutineDevices() {
        return routineDevices;
    }
    public void setRoutineDevices(List<RoutineUpdateDeviceRequest> routineDevices) {
        this.routineDevices = routineDevices;
    }
}
