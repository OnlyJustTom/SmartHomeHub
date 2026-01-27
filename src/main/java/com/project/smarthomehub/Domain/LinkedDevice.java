package com.project.smarthomehub.Domain;

import jakarta.persistence.*;


@Entity
public class LinkedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    //Links UserID and DeviceID together in a new table
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // Getters & setters
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

}
