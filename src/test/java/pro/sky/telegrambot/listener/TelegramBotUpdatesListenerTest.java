package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.services.MessageParserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {

    @InjectMocks
    private TelegramBotUpdatesListener listener;

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private MessageParserService parserService;

    @BeforeEach
    void setUp() {
        given(parserService.processMessage(any(String.class), any(Message.class))).willDoNothing();
    }

    @Test
    void shouldHandleStartCommand() {
        Update update = createUpdateWithText("/start");
        List<Update> updates = Collections.singletonList(update);

        listener.process(updates);

        verify(telegramBot).execute(new SendMessage(update.message().chat().id(), "Привет! Это мой первый телеграм-бот."));
    }

    @Test
    void shouldHandleRemindMeCommand() throws Exception {
        Update update = createUpdateWithText("/remindme завтра сделать доклад");
        List<Update> updates = Collections.singletonList(update);

        listener.process(updates);

        verify(parserService).processMessage("/remindme завтра сделать доклад", update.message());
        verify(telegramBot).execute(new SendMessage(update.message().chat().id(), "Напоминание успешно установлено!"));
    }

    @Test
    void shouldHandleErrorInRemindMeCommand() throws Exception {
        given(parserService.processMessage(any(String.class), any(Message.class)))
                .willThrow(new IllegalArgumentException("Некорректный формат даты"));

        Update update = createUpdateWithText("/remindme неправильно указан срок");
        List<Update> updates = Collections.singletonList(update);

        listener.process(updates);

        verify(telegramBot).execute(new SendMessage(update.message().chat().id(),
                "Ошибка создания напоминания: Некорректный формат даты"));
    }

    @Test
    void shouldIgnoreEmptyMessages() {
        Update update = createUpdateWithText("");
        List<Update> updates = Collections.singletonList(update);

        listener.process(updates);

        verify(telegramBot, times(0)).execute(any(SendMessage.class));
    }

    @Test
    void shouldIgnoreUnknownMessages() {
        Update update = createUpdateWithText("Что-то другое");
        List<Update> updates = Collections.singletonList(update);

        listener.process(updates);

        verify(telegramBot, times(0)).execute(any(SendMessage.class));
    }

    private Update createUpdateWithText(String text) {
        Message message = new Message()
                .setText(text)
                .setChat(new com.pengrad.telegrambot.model.Chat().setId(123));
        return new Update().setMessage(message);
    }
}