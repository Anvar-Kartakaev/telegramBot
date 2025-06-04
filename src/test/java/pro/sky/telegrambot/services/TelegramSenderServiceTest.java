package pro.sky.telegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TelegramSenderServiceTest {

    @InjectMocks
    private TelegramSenderService service;

    @Mock
    private TelegramLongPollingBot mockedBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendTelegramMessage_Success() throws TelegramApiException {
        long chatId = -1L;
        String text = "Привет!";

        doNothing().when(mockedBot).execute(any(SendMessage.class));

        service.sendTelegramMessage(chatId, text);

        verify(mockedBot).execute(any(SendMessage.class));
    }

    @Test
    void testSendTelegramMessage_ErrorHandling() throws TelegramApiException {
        long chatId = -1L;
        String text = "Это сообщение нельзя отправить.";

        doThrow(new TelegramApiException("Ошибка отправки")).when(mockedBot).execute(any(SendMessage.class));

        assertThatThrownBy(() -> service.sendTelegramMessage(chatId, text))
                .isInstanceOf(TelegramApiException.class)
                .hasMessageContaining("Ошибка отправки");
    }

    @Test
    void testSafeSendTelegramMessage_Success() throws TelegramApiException {
        long chatId = -1L;
        String text = "Безопасное сообщение";

        doNothing().when(mockedBot).execute(any(SendMessage.class));

        boolean result = service.safeSendTelegramMessage(chatId, text);

        assertThat(result).isTrue();
        verify(mockedBot).execute(any(SendMessage.class));
    }

    @Test
    void testSafeSendTelegramMessage_Failure() throws TelegramApiException {
        long chatId = -1L;
        String text = "Неудачная попытка отправки";

        doThrow(new TelegramApiException("Ошибка")).when(mockedBot).execute(any(SendMessage.class));

        boolean result = service.safeSendTelegramMessage(chatId, text);

        assertThat(result).isFalse();
        verify(mockedBot).execute(any(SendMessage.class));
    }
}