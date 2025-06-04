package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    private UUID id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "message_text", length = 255, nullable = false)
    private String messageText;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    public NotificationTask() {}

    public NotificationTask(UUID id, Long chatId, String messageText, LocalDateTime sendAt) {
        this.id = id;
        this.chatId = chatId;
        this.messageText = messageText;
        this.sendAt = sendAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public LocalDateTime getSendAt() { return sendAt; }
    public void setSendAt(LocalDateTime sendAt) { this.sendAt = sendAt; }
}