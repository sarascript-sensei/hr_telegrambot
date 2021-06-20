package com.example.abu.abu_zver.bot.handler;

import com.example.abu.abu_zver.bot.State;
import com.example.abu.abu_zver.model.Position;
import com.example.abu.abu_zver.model.Resume;
import com.example.abu.abu_zver.model.User;
import com.example.abu.abu_zver.repository.PositionRepository;
import com.example.abu.abu_zver.repository.ResumeRepository;
import com.example.abu.abu_zver.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.abu.abu_zver.bot.handler.NameHandler.NAME_START;
import static com.example.abu.abu_zver.bot.handler.SkillHandler.SKILL_CHOOSE_START;
import static com.example.abu.abu_zver.utils.MessageUtil.getMessageToText;

@Component
public class HrTypeHandler implements Handler {
    public static final String HR_CHOOSE_FRONT = "/hr_front";
    public static final String HR_CHOOSE_BACK = "/hr_back";
    public static final String HR_CHOOSE_START = "/hr_start";
    public static final String HR_CHOOSE = "// ";

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public HrTypeHandler(PositionRepository positionRepository, UserRepository userRepository, ResumeRepository resumeRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Object message) {
        if(getMessageToText(message).equalsIgnoreCase(HR_CHOOSE_START))
            return start(user, getMessageToText(message));
        else if(getMessageToText(message).equalsIgnoreCase(HR_CHOOSE_BACK))
            return chooseBack(user, getMessageToText(message));
        else if(getMessageToText(message).equalsIgnoreCase(HR_CHOOSE_FRONT))
            return chooseFront(user, getMessageToText(message));

        return check(user, getMessageToText(message));
    }

    private List<PartialBotApiMethod<? extends Serializable>> pdf(User user, String message) {
        var resumes = resumeRepository.findAll();
        Resume resume = null;
        for(var r : resumes) {
            if(r.getId().toString().equals(message))
                resume = r;
        }

        if(resume == null)
            return null;

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(user.getChatId()));
        sendDocument.setDocument(new InputFile(resume.getPdf()));

        return List.of(sendDocument);
    }

    private List<PartialBotApiMethod<? extends Serializable>> check(User user, String message) {
        System.out.println(message);
        var resumes = resumeRepository.findAll();
        Resume resume = null;
        var name = message.replace("//", "");

        if(isNumeric(name.replace(" ", ""))) {
            return pdf(user, name.replace(" ", ""));
        }

        for(var r : resumes) {
            if(r.getName().equals(name.replace(" ", "")))
                resume = r;
        }

        if(resume == null)
            return null;

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var acceptButton = new InlineKeyboardButton();
        acceptButton.setText("PDF");
        acceptButton.setCallbackData("// " + resume.getId());

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(acceptButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText(
                "Your information: \n"
                        + "Name: " + resume.getName() + "\n"
                        + "Position: " + resume.getPosition() + "\n"
                        + "Skills: " + resume.getSkills() + "\n"
                        + "Work experience: " + resume.getExperience() + "\n"
                        + "About you: " + resume.getAbout() + "\n"
        );
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> chooseFront(User user, String messageToText) {
        List<Resume> resumes = resumeRepository.findAll();

        resumes.removeIf(r -> r.getType().equals("Backend"));

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

        for(var r : resumes) {
            var button = new InlineKeyboardButton();
            button.setText(r.getName());
            button.setCallbackData("// " + r.getName());

            buttons.add(button);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        inlineKeyboardMarkup.setKeyboard(List.of(buttons));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Frontend developers:");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> chooseBack(User user, String messageToText) {
        List<Resume> resumes = resumeRepository.findAll();

        resumes.removeIf(r -> r.getType().equals("Frontend"));

        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

        for(var r : resumes) {
            var button = new InlineKeyboardButton();
            button.setText(r.getName());
            button.setCallbackData("// " + r.getName());

            buttons.add(button);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        inlineKeyboardMarkup.setKeyboard(List.of(buttons));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Backend developers:");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private List<PartialBotApiMethod<? extends Serializable>> start(User user, String message) {
        user.setBotState(State.HR_CHOOSE_ROLE);
        userRepository.save(user);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var devButton = new InlineKeyboardButton();
        devButton.setText("Backend");
        devButton.setCallbackData(HR_CHOOSE_BACK);

        var hrButton = new InlineKeyboardButton();
        hrButton.setText("Frontend");
        hrButton.setCallbackData(HR_CHOOSE_FRONT);

        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(devButton, hrButton);

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getChatId()));
        sendMessage.setText("Choose Developer life style);");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(sendMessage);
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public State operatedBotState() {
        return State.HR_CHOOSE_ROLE;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(HR_CHOOSE_BACK, HR_CHOOSE_FRONT, HR_CHOOSE_START, HR_CHOOSE);
    }
}
