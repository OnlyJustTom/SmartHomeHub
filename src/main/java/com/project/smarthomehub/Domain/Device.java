package com.project.smarthomehub.Domain;

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
    //private DeviceType type;
    private String APIKey;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<LinkedDevice> users = new ArrayList<>();

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
//    public DeviceType getType() {
//        return type;
//    }
//    public void setType(DeviceType type) {
//        this.type = type;
//    }
    public String getAPIKey() {
        return APIKey;
    }
    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }
    public List<LinkedDevice> getUsers() {
        return users;
    }
    public void setUsers(List<LinkedDevice> users) {
        this.users = users;
    }
}
