package com.project.smarthomehub.Helpers;

import java.io.Serializable;
import java.util.Objects;

public class RoutineUserId implements Serializable {

    private Integer routine;
    private Integer user;

    public RoutineUserId() {}

    public RoutineUserId(Integer routine, Integer user) {
        this.routine = routine;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoutineUserId)) return false;
        RoutineUserId that = (RoutineUserId) o;
        return routine.equals(that.routine) && user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routine, user);
    }

}
