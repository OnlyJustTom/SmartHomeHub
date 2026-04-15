package com.project.smarthomehub.Domain;

import com.project.smarthomehub.Helpers.RoutineUserId;

import jakarta.persistence.*;

@Entity
@IdClass(RoutineUserId.class)
public class RoutineUsers {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters & setters
    public Routine getRoutine() {
        return routine;
    }
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
