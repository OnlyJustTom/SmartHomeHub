package com.project.smarthomehub.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.smarthomehub.TriggerCondition;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Trigger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trigger_device_id", nullable = false)
    private Device sourceDevice;

    @Enumerated(EnumType.STRING)
    private TriggerCondition triggerCondition;

    @OneToMany(mappedBy = "trigger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TriggerDevices> targetDevices = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Device getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(Device triggerDevice) {
        this.sourceDevice = triggerDevice;
    }

    public TriggerCondition getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(TriggerCondition triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public List<TriggerDevices> getTargetDevices() {
        return targetDevices;
    }

    public void setTargetDevices(List<TriggerDevices> targetDevices) {
        this.targetDevices = targetDevices;
    }


}
