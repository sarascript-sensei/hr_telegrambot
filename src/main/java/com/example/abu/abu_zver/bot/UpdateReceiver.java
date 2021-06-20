package com.example.abu.abu_zver.bot;

import com.example.abu.abu_zver.bot.handler.Handler;
import com.example.abu.abu_zver.model.Resume;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.PdfHandler.FINISH;
import static com.example.abu.abu_zver.bot.handler.RoleHandler.ROLE_CHOOSE_START;

@Component
public class UpdateReceiver {

    public static final String CANCEL = "/cancel";

    // Storing available handlers in a list (stolen from Miroha)
    private final List<Handler> handlers;
    // Achieving access to user storage
    private final UserRepository userRepository;

    public UpdateReceiver(List<Handler> handlers, UserRepository userRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
    }

    // Analyzing received update
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if(update.hasMessage() && update.getMessage().hasDocument()) {
                Resume.resume.setPdf(update.getMessage().getDocument().getFileId());
                System.out.println("Document added " + update.getMessage().getDocument().getFileId());
                return pdf(update.getMessage());
            }
            // Checking if Update is a message with text
            if (isMessageWithText(update)) {
                // Getting Message from Update
                final Message message = update.getMessage();
                // Getting chatId
                final long chatId = message.getFrom().getId();

                // Getting user from repository. If user is not presented in repository - create new and return him
                // For this reason we have a one arg constructor in User.class
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));

                if(message.getText().equalsIgnoreCase(CANCEL) || message.getText().equalsIgnoreCase("/start"))
                    return cancel(user);

                System.out.println(user);

                // Looking for suitable handler
                return getHandlerByState(user.getBotState()).handle(user, message);
                // Same workflow but for CallBackQuery
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getFrom().getId();
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));

                System.out.println(user);

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery);
            }

            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> pdf(Message message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Next");
        acceptButton.setCallbackData(FINISH);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText("pdf succesfully loaded");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setBotState(State.START);
        userRepository.save(user);

        Resume.resume.clear();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var startButton = new InlineKeyboardButton();
        startButton.setText("Start");
        startButton.setCallbackData(ROLE_CHOOSE_START);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(startButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Click Start if you wanna start");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private Handler getHandlerByState(State state) {
        return handlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return handlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
