package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, UUID> {

    List<NotificationTask> findAllBySendAt(LocalDateTime sendAt);
}