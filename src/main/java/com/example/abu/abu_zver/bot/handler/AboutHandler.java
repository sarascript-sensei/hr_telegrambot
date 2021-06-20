package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.Resume;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.PdfHandler.PDF_START;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class AboutHandler implements Handler {
    public static final String ABOUT_START = "/about_start";

    private final UserRepository userRepository;

    public AboutHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        if (getMessageToText(message).equalsIgnoreCase(ABOUT_START))
            return start(user, getMessageToText(message));

        return check(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> check(User user, String message) {
        Resume.resume.setAbout(message);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Accept");
        acceptButton.setCallbackData(PDF_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("About you: " + Resume.resume.getAbout() + "\nPlease Accept information");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> start(User user, String message) {
        user.setBotState(State.ENTER_ABOUT);
        userRepository.save(user);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Please tell us about you: ");

        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.ENTER_ABOUT;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(ABOUT_START);
    }
}
