package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TelegramBotConfigurationTest {

    @Autowired
    private TelegramBotConfiguration config;

    @Test
    void testTelegramBotInitialization() {
        TelegramBot bot = config.telegramBot();
        assertThat(bot).isNotNull();
    }
}