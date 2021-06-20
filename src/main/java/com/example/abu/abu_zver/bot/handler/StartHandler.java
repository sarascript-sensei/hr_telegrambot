package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.RoleHandler.ROLE_CHOOSE_START;

@Component
public class StartHandler implements Handler {
    @Value("${bot.name}")
    private String botUsername;

    private final UserRepository userRepository;

    public StartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        // Welcoming user
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var startButton = new InlineKeyboardButton();
        startButton.setText("Start");
        startButton.setCallbackData(ROLE_CHOOSE_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(startButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage welcomeMessage = new SendMessage();
        welcomeMessage.setChatId(String.valueOf(user.getChatId()));
        welcomeMessage.setText(String.format(
                        "Hola! I'm *%s*%nI am here to help you", botUsername
                ));
        welcomeMessage.enableMarkdown(true);
        welcomeMessage.setReplyMarkup(inlineKeyboardMarkup);

        // Changing user state to "awaiting for name input"
        user.setBotState(State.CHOOSE_ROLE);
        userRepository.save(user);

        return List.of(welcomeMessage);
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
