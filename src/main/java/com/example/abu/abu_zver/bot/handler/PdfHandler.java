package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.Resume;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.ResumeRepository;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.List;

import static com.example.abu.abu_zver.bot.UpdateReceiver.CANCEL;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class PdfHandler implements Handler {
    public static final String PDF_START = "/pdf_start";
    public static final String FINISH = "/finish";
    public static final String FINISH2 = "/finish2";

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public PdfHandler(UserRepository userRepository, ResumeRepository resumeRepository) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        if (getMessageToText(message).equalsIgnoreCase(PDF_START))
            return start(user, getMessageToText(message));
        else if (getMessageToText(message).equalsIgnoreCase(FINISH))
            return check(user, getMessageToText(message));
        else if (getMessageToText(message).equalsIgnoreCase(FINISH2))
            return finish(user, getMessageToText(message));
        else if(Resume.resume.getPdf() == null)
            return start(user, getMessageToText(message));

        return start(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> finish(User user, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Thats all. Have a good day ;)\nType /start to start The bot");

        user.setBotState(State.START);
        userRepository.save(user);

        resumeRepository.save(Resume.resume);
        Resume.resume.clear();

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> check(User user, String message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("Finish");
        acceptButton.setCallbackData(FINISH2);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText(
                "Your information: \n"
                + "Name: " + Resume.resume.getName() + "\n"
                + "Position: " + Resume.resume.getPosition() + "\n"
                + "Skills: " + Resume.resume.getSkills() + "\n"
                + "Work experience: " + Resume.resume.getExperience() + "\n"
                + "About you: " + Resume.resume.getAbout() + "\n"
        );
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> start(User user, String message) {
        user.setBotState(State.UPLOAD_PDF);
        userRepository.save(user);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Please upload your CV as pdf: ");

        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.UPLOAD_PDF;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(PDF_START, FINISH, FINISH2);
    }
}
