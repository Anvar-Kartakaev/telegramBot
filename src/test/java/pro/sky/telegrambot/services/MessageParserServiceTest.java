package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageParserServiceTest {

    @InjectMocks
    private MessageParserService service;

    @Mock
    private NotificationTaskRepository notificationTaskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessMessage_ValidInput_SavesToRepository() throws Exception {
        Chat chat = new Chat();
        chat.id();
        Message message = new Message();
        message.chat();

        String validCommand = "/remindme buy milk at 2025-12-31 18:00";

        service.processMessage(validCommand, message);

        verify(notificationTaskRepository).save(any(NotificationTask.class));
    }

    @Test
    void testProcessMessage_InvalidFormat_ExceptionThrown() {
        Chat chat = new Chat();
        chat.id();
        Message message = new Message();
        message.chat();

        String invalidCommand = "/remindme buy milk";

        assertThatThrownBy(() ->
                service.processMessage(invalidCommand, message)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Некорректный формат команды напоминания");
    }

    @Test
    void testProcessMessage_WrongDatetime_FormatExceptionThrown() {
        Chat chat = new Chat();
        chat.id();
        Message message = new Message();
        message.chat();

        String wrongDatetimeCommand = "/remindme buy milk at 2025-13-31 18:00";

        assertThatThrownBy(() ->
                service.processMessage(wrongDatetimeCommand, message)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ошибка при обработке даты и времени");
    }

    @Test
    void testProcessMessage_GeneratedUUIDIsUnique() throws Exception {
        Chat chat = new Chat();
        chat.id();
        Message message = new Message();
        message.chat();

        String command1 = "/remindme buy milk at 2025-12-31 18:00";
        String command2 = "/remindme call doctor at 2025-12-31 17:00";

        service.processMessage(command1, message);
        service.processMessage(command2, message);

        verify(notificationTaskRepository, times(2)).save(any(NotificationTask.class));
    }

    @Test
    void testProcessMessage_ParsingAndSavingCorrectly() throws Exception {
        Chat chat = new Chat();
        chat.id();
        Message message = new Message();
        message.chat();

        String command = "/remindme walk dog at 2025-12-31 18:00";

        given(notificationTaskRepository.save(any()))
                .willAnswer(invocation -> invocation.getArguments()[0]);

        service.processMessage(command, message);

        Optional<NotificationTask> taskOptional = notificationTaskRepository.findAll()
                .stream()
                .findFirst();

        assertThat(taskOptional.isPresent()).isTrue();
        NotificationTask task = taskOptional.get();
        assertThat(task.getChatId()).isEqualTo(-1L);
        assertThat(task.getMessageText()).isEqualTo("walk dog");
        assertThat(task.getSendAt()).isEqualTo(LocalDateTime.of(2025, 12, 31, 18, 0));
    }
}