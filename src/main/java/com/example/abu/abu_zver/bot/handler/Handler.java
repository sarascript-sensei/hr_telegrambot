package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.User;
import org.aspectj.bridge.Message;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message);

    State operatedBotState();

    List<String> operatedCallBackQuery();
}
