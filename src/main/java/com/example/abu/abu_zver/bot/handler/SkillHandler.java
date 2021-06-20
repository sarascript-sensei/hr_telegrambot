package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.Position;
import com.example.abu.abu_zver.model.Resume;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.PositionRepository;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.ExpHandler.EXP_START;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class SkillHandler implements Handler {
    public static final String SKILL_CHOOSE_START = "/skill_choose_start";

    private final UserRepository userRepository;

    public SkillHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        if (getMessageToText(message).equalsIgnoreCase(SKILL_CHOOSE_START))
            return start(user, getMessageToText(message));

        return check(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> check(User user, String message) {
        Resume.resume.setSkills(message);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Accept");
        acceptButton.setCallbackData(EXP_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your skills: " + Resume.resume.getSkills() + "\nPlease Accept your skills");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> start(User user, String message) {
        user.setBotState(State.CHOOSE_SKILL);
        userRepository.save(user);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Please type your skills (SQL, Flutter, Git etc.): ");

        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.CHOOSE_SKILL;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(SKILL_CHOOSE_START);
    }
}
