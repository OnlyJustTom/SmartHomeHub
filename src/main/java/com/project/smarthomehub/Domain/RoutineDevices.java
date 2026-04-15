package com.project.smarthomehub.Domain;

import com.project.smarthomehub.CommandType;
import jakarta.persistence.*;

import com.project.smarthomehub.Helpers.RoutineDeviceId;

@Entity
@IdClass(RoutineDeviceId.class)
public class RoutineDevices {

    @Id
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Id
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    private CommandType commandType;

    private String commandData;

    // Getters & setters
    public Routine getRoutine() {
        return routine;
    }
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }
    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
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
