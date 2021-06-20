package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.HrTypeHandler.HR_CHOOSE_START;
import static com.example.abu.abu_zver.bot.handler.NameHandler.NAME_START;
import static com.example.abu.abu_zver.bot.handler.TypeHandler.TYPE_CHOOSE_START;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class RoleHandler implements Handler {
    public static final String ROLE_CHOOSE_START = "/role_choose_start";

    private final UserRepository userRepository;

    public RoleHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        return error(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> error(User user, String message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var devButton = new InlineKeyboardButton();
        devButton.setText("Developer");
        devButton.setCallbackData(NAME_START);

        var hrButton = new InlineKeyboardButton();
        hrButton.setText("HR");
        hrButton.setCallbackData(HR_CHOOSE_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(devButton, hrButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Choose who are you");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }


    @Override
    public State operatedBotState() {
        return State.CHOOSE_ROLE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(ROLE_CHOOSE_START);
    }
}
