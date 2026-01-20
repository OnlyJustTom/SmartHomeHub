package com.project.smarthomehub.Domain;

import jakarta.persistence.*;


@Entity
public class LinkedDevice {

    //TODO - This below
    //This is a table that links users to devices allowing for multiple users to link different devices to the hub
    //Composite key of User ID and Device ID
    //One-to-Many relationship between User and Device -> One user can have Many devices linked to them

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
