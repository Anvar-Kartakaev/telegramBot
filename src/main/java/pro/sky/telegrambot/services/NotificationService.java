package pro.sky.telegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTaskRepository repository;
    private final TelegramSenderService senderService;

    public void createNewTask(NotificationTask task) {
        repository.save(task);
    }

    public Optional<NotificationTask> findTaskById(UUID id) {
        return repository.findById(id);
    }

    public void sendPendingNotifications() {
        LocalDateTime currentMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> tasksForCurrentMinute = repository.findAllBySendAt(currentMinute);

        for (NotificationTask task : tasksForCurrentMinute) {
            try {
                senderService.sendTelegramMessage(task.getChatId(), task.getMessageText());

                repository.delete(task);
            } catch (Exception ex) {
                System.err.println("Ошибка отправки уведомления: " + ex.getMessage());
            }
        }
    }
}