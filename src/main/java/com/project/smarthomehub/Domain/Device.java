package com.project.smarthomehub.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.smarthomehub.DeviceType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private DeviceType type;
    private String APIKeyIP;

    @JsonIgnore
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<LinkedDevice> users = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDevices> routines = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sourceDevice", cascade = CascadeType.ALL)
    private List<Trigger> triggerSources = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TriggerDevices> triggerTargets = new ArrayList<>();


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public DeviceType getType() {
        return type;
    }
    public void setType(DeviceType type) {
        this.type = type;
    }
    public String getAPIKeyIP() {
        return APIKeyIP;
    }
    public void setAPIKeyIP(String APIKeyIP) {
        this.APIKeyIP = APIKeyIP;
    }
    public List<LinkedDevice> getUsers() {
        return users;
    }
    public void setUsers(List<LinkedDevice> users) {
        this.users = users;
    }
    public List<RoutineDevices> getRoutines() {
        return routines;
    }
    public void setRoutines(List<RoutineDevices> routines) {
        this.routines = routines;
    }

    public List<Trigger> getTriggerSources() {
        return triggerSources;
    }

    public void setTriggerSources(List<Trigger> triggerSources) {
        this.triggerSources = triggerSources;
    }

    public List<TriggerDevices> getTriggerTargets() {
        return triggerTargets;
    }

    public void setTriggerTargets(List<TriggerDevices> triggerTargets) {
        this.triggerTargets = triggerTargets;
    }
}
