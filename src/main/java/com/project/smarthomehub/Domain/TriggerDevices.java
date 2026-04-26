package com.project.smarthomehub.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.smarthomehub.CommandType;
import com.project.smarthomehub.Helpers.TriggerDeviceId;
import jakarta.persistence.*;

@Entity
@Table(name = "trigger_devices")
@IdClass(TriggerDeviceId.class)
public class TriggerDevices {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_id", nullable = false)
    @JsonIgnore
    private Trigger trigger;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    private CommandType commandType;

    private String commandData;

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
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

