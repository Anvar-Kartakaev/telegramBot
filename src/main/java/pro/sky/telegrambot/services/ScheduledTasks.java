package pro.sky.telegrambot.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final NotificationService notificationService;

    public ScheduledTasks(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void scheduleNotifications() {
        notificationService.sendPendingNotifications();
    }
}