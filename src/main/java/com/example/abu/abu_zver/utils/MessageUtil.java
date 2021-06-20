package com.example.abu.abu_zver.utils;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageUtil {
    public static String getMessageToText(Object message) {
        return message instanceof Message ? ((Message) message).getText() : ((CallbackQuery) message).getData();
    }
}
