package com.example.abu.abu_zver.model;

import com.example.abu.abu_zver.bot.State;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Resume extends AbstractBaseEntity {
    public static final Resume resume = new Resume();

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String type;

    @NotNull
    @NotEmpty
    private String position;

    @NotNull
    @NotEmpty
    private String skills;

    @NotNull
    @NotEmpty
    private String experience;

    @NotNull
    @NotEmpty
    private String about;

    @NotNull
    @NotEmpty
    private String pdf;

    public Resume() {
    }

    public Resume(String name, String type, String position, String skills, String experience, String about, State botState, String pdf) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.skills = skills;
        this.experience = experience;
        this.about = about;
        this.pdf = pdf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public static Resume getResume() {
        return resume;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public void clear() {
        resume.setPdf(null);
        resume.setPosition(null);
        resume.setName(null);
        resume.setSkills(null);
        resume.setExperience(null);
        resume.setAbout(null);
        resume.setType(null);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "type='" + type + '\'' +
                ", position=" + position +
                ", skills=" + skills +
                ", experience='" + experience + '\'' +
                ", about='" + about + '\'' +
                ", pdf='" + pdf + '\'' +
                '}';
    }
}

