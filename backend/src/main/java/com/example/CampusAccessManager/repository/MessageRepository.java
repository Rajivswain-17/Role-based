package com.example.CampusAccessManager.repository;
import com.example.CampusAccessManager.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientTypeOrderBySentAtDesc(String recipientType);
    List<Message> findByRecipientUsernameOrderBySentAtDesc(String username);
    List<Message> findBySenderUsernameOrderBySentAtDesc(String senderUsername);
}



