package com.example.abu.abu_zver.model;

import com.example.abu.abu_zver.bot.State;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User extends AbstractBaseEntity {
    @NotNull
    @NotEmpty
    private long chatId;

    @NotNull
    @NotEmpty
    private State botState;

    public User(long chatId) {
        this.chatId = chatId;
        this.botState = State.START;
    }

    public User() {

    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public State getBotState() {
        return botState;
    }

    public void setBotState(State botState) {
        this.botState = botState;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", botState=" + botState +
                '}';
    }
}

