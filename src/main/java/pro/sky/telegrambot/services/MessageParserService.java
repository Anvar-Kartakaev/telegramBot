package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class MessageParserService {

    private static final Pattern REMINDER_PATTERN = Pattern.compile("/remindme (.+) at ([^ ]+)$");

    private final NotificationTaskRepository repository;

    public MessageParserService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    public void processMessage(String inputMessage, Message message) {
        Matcher matcher = REMINDER_PATTERN.matcher(inputMessage);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Некорректный формат команды напоминания");
        }

        String reminderText = matcher.group(1).trim();
        String datetimeStr = matcher.group(2).trim();

        LocalDateTime sendAt = LocalDateTime.parse(datetimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        NotificationTask task = new NotificationTask();
        task.setId(UUID.randomUUID());
        task.setChatId(message.chat().id());
        task.setMessageText(reminderText);
        task.setSendAt(sendAt);

        repository.save(task);
    }
}