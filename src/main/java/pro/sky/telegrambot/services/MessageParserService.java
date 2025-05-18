package pro.sky.telegrambot.services;

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

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");

    private final NotificationTaskRepository repository;

    public MessageParserService(NotificationTaskRepository repository) {
        this.repository = repository;
    }

    public void processMessage(String inputMessage) {
        Matcher matcher = MESSAGE_PATTERN.matcher(inputMessage.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат сообщения");
        }

        String dateString = matcher.group(1).trim();  // дата-время
        String reminderText = matcher.group(3).trim();  // текст напоминания

        LocalDateTime sendAt = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        NotificationTask task = new NotificationTask();
        task.setId(UUID.randomUUID());
        task.setChatId(123456789L);
        task.setMessageText(reminderText);
        task.setSendAt(sendAt);

        repository.save(task);
    }
}