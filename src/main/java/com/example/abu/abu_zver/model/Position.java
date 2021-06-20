package com.example.abu.abu_zver.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Position extends AbstractBaseEntity {
    @NotNull
    @NotEmpty
    private String name;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    public Position() {
    }

    public Position(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Position{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
