package com.example.abu.abu_zver.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Skill extends AbstractBaseEntity {
    @NotNull
    @NotEmpty
    private String name;

    @ManyToOne
    @JoinColumn(name="position_id")
    private Position position;

    public Skill() {
    }

    public Skill(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
