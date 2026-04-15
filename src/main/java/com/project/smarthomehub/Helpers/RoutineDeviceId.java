package com.project.smarthomehub.Helpers;

import java.io.Serializable;
import java.util.Objects;

public class RoutineDeviceId implements Serializable {

    private Integer routine;
    private Integer device;

    public RoutineDeviceId() {}

    public RoutineDeviceId(Integer routine, Integer device) {
        this.routine = routine;
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutineDeviceId)) return false;
        RoutineDeviceId that = (RoutineDeviceId) o;
        return routine.equals(that.routine) && device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routine, device);
    }

}
