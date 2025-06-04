package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.services.MessageParserService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private MessageParserService parserService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Message message = update.message();
            if (message != null && message.text() != null) {
                String text = message.text();
                if (text.equals("/start")) {
                    SendMessage welcomeMsg = new SendMessage(message.chat().id(), "Привет! Это мой первый телеграм-бот.");
                    telegramBot.execute(welcomeMsg);
                } else if (text.startsWith("/remindme")) {
                    try {
                        parserService.processMessage(text, message);
                        SendMessage successMsg = new SendMessage(message.chat().id(), "Напоминание успешно установлено!");
                        telegramBot.execute(successMsg);
                    } catch (IllegalArgumentException e) {
                        SendMessage errorMsg = new SendMessage(message.chat().id(), "Ошибка создания напоминания: " + e.getMessage());
                        telegramBot.execute(errorMsg);
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}