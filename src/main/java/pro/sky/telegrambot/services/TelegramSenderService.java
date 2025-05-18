package pro.sky.telegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramSenderService {

    @lombok.Getter
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botUsername;

    private TelegramLongPollingBot getTelegramBot() {
        return new TelegramLongPollingBot() {
            @Override
            public void onUpdateReceived(Update update) {
            }

            @Override
            public String getBotToken() {
                return botToken;
            }

            @Override
            public String getBotUsername() {
                return botUsername;
            }
        };
    }

    public void sendTelegramMessage(long chatId, String text) throws TelegramApiException {
        TelegramLongPollingBot telegramBot = getTelegramBot();
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        telegramBot.execute(message);
    }

    public boolean safeSendTelegramMessage(long chatId, String text) {
        try {
            sendTelegramMessage(chatId, text);
            return true;
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            return false;
        }
    }
}