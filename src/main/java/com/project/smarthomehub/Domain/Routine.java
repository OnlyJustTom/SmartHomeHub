package com.project.smarthomehub.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private LocalTime timeToExecute;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> daysToExecute;
    private boolean isEnabled;
    private LocalDateTime lastExecuted;

    @JsonIgnore
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDevices> routineDevices = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineUsers> routineUsers = new ArrayList<>();

    // Getters & setters
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

    public LocalTime getTimeToExecute() {
        return timeToExecute;
    }
    public void setTimeToExecute(LocalTime timeToExecute) {
        this.timeToExecute = timeToExecute;
    }

    @Enumerated(EnumType.STRING)
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

    public List<RoutineDevices> getRoutineDevices() {
        return routineDevices;
    }
    public void setRoutineDevices(List<RoutineDevices> routineDevices) {
        this.routineDevices = routineDevices;
    }

    public List<RoutineUsers> getRoutineUsers() {
        return routineUsers;
    }
    public void setRoutineUsers(List<RoutineUsers> routineUsers) {
        this.routineUsers = routineUsers;
    }
    public LocalDateTime getLastExecuted() {
        return lastExecuted;
    }
    public void setLastExecuted(LocalDateTime lastExecuted) {
        this.lastExecuted = lastExecuted;
    }
}
