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

import static com.example.abu.abu_zver.bot.handler.SkillHandler.SKILL_CHOOSE_START;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class TypeHandler implements Handler {
    public static final String TYPE_CHOOSE_FRONT = "/type_choose_front";
    public static final String TYPE_CHOOSE_BACK = "/type_choose_back";
    public static final String TYPE_CHOOSE_START = "/type_choose_start";
    public static final String TYPE_CHOOSE_ACCEPT = "/ ";

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public TypeHandler(PositionRepository positionRepository, UserRepository userRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        if (getMessageToText(message).equalsIgnoreCase(TYPE_CHOOSE_FRONT))
            return chooseFront(user);
        else if (getMessageToText(message).equalsIgnoreCase(TYPE_CHOOSE_BACK))
            return chooseBack(user);
        else if (getMessageToText(message).equalsIgnoreCase(TYPE_CHOOSE_START))
            return error(user, getMessageToText(message));
        else if (getMessageToText(message).equalsIgnoreCase(TYPE_CHOOSE_ACCEPT))
            return accept(user, getMessageToText(message));


        return custom(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> accept(User user, String message) {
        Resume.resume.setPosition(message.replace("/", ""));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Accept");
        acceptButton.setCallbackData(SKILL_CHOOSE_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your position: " + Resume.resume.getPosition() + "\nPlease Accept your position");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);

    }

    private List<PartialBotApiMethod<? extends Serializable>> custom(User user, String message) {
        Resume.resume.setPosition(message);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Accept");
        acceptButton.setCallbackData(SKILL_CHOOSE_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your position: " + Resume.resume.getPosition() + "\nPlease Accept your position");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> chooseFront(User user) {
        Resume.resume.setType("Frontend");

        List<Position> positions = positionRepository.findAll();
        positions.removeIf(p -> p.getType().getName().equals("Backend"));

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

        for(var p : positions) {
            var button = new InlineKeyboardButton();
            button.setText(p.getName());
            button.setCallbackData("/ " + p.getName());

            buttons.add(button);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        inlineKeyboardMarkup.setKeyboard(List.of(buttons));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your Frontend position\nIf position doesn't exist, just type it ;)");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> chooseBack(User user) {
        Resume.resume.setType("Backend");

        List<Position> positions = positionRepository.findAll();
        positions.removeIf(p -> p.getType().getName().equals("Frontend"));

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

        for(var p : positions) {
            var button = new InlineKeyboardButton();
            button.setText(p.getName());
            button.setCallbackData("/ " + p.getName());

            buttons.add(button);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        inlineKeyboardMarkup.setKeyboard(List.of(buttons));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your Backend position\nIf position doesn't exist, just type it ;)");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> error(User user, String message) {
        user.setBotState(State.CHOOSE_TYPE);
        userRepository.save(user);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var frontButton = new InlineKeyboardButton();
        frontButton.setText("Frontend");
        frontButton.setCallbackData(TYPE_CHOOSE_FRONT);

        var backButton = new InlineKeyboardButton();
        backButton.setText("Backend");
        backButton.setCallbackData(TYPE_CHOOSE_BACK);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(frontButton, backButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Your life position");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }


    @Override
    public State operatedBotState() {
        return State.CHOOSE_TYPE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(TYPE_CHOOSE_BACK, TYPE_CHOOSE_FRONT, TYPE_CHOOSE_START, TYPE_CHOOSE_ACCEPT);
    }
}
