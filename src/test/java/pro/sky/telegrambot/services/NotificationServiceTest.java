package pro.sky.telegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService service;

    @Mock
    private NotificationTaskRepository repository;

    @Mock
    private TelegramSenderService telegramSenderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewTask() {
        NotificationTask task = new NotificationTask();
        task.setId(UUID.randomUUID());
        task.setChatId(-1L);
        task.setMessageText("Напоминание");
        task.setSendAt(LocalDateTime.now());

        service.createNewTask(task);

        verify(repository).save(eq(task));
    }

    @Test
    void testFindTaskById_Successful() {
        UUID taskId = UUID.randomUUID();
        NotificationTask expectedTask = new NotificationTask();
        expectedTask.setId(taskId);
        expectedTask.setChatId(-1L);
        expectedTask.setMessageText("Сообщение");
        expectedTask.setSendAt(LocalDateTime.now());

        given(repository.findById(taskId))
                .willReturn(Optional.of(expectedTask));

        Optional<NotificationTask> result = service.findTaskById(taskId);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(taskId);
    }

    @Test
    void testFindTaskById_NotFound() {
        UUID nonExistingTaskId = UUID.randomUUID();
        given(repository.findById(nonExistingTaskId))
                .willReturn(Optional.empty());

        Optional<NotificationTask> result = service.findTaskById(nonExistingTaskId);

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void testSendPendingNotifications() throws TelegramApiException {
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        List<NotificationTask> pendingTasks = new ArrayList<>();

        NotificationTask task1 = new NotificationTask();
        task1.setId(UUID.randomUUID());
        task1.setChatId(-1L);
        task1.setMessageText("Сообщение 1");
        task1.setSendAt(now);

        NotificationTask task2 = new NotificationTask();
        task2.setId(UUID.randomUUID());
        task2.setChatId(-2L);
        task2.setMessageText("Сообщение 2");
        task2.setSendAt(now.plusMinutes(1));

        pendingTasks.add(task1);
        pendingTasks.add(task2);

        given(repository.findAllBySendAt(now))
                .willReturn(pendingTasks.subList(0, 1));

        service.sendPendingNotifications();

        verify(telegramSenderService).sendTelegramMessage(eq(-1L), eq("Сообщение 1"));
        verify(repository).delete(eq(task1));
        verify(repository, times(0)).delete(eq(task2));
    }
}