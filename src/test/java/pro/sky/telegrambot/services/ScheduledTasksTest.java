package pro.sky.telegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.mockito.Mockito.verify;

@SpringBootTest
class ScheduledTasksTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleNotifications() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        scheduledTasks.scheduleNotifications();

        verify(notificationService).sendPendingNotifications();
    }

    @Test
    void testCronExpression() throws NoSuchMethodException {
        Scheduled annotation = scheduledTasks.getClass().getMethod("scheduleNotifications").getAnnotation(Scheduled.class);

        assert annotation.cron().equals("0 */1 * * * *");
    }
}