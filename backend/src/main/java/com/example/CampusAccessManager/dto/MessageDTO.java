package com.example.CampusAccessManager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String subject;
    private String message;
    private String senderUsername;
    private LocalDateTime sentAt;
    private String recipientType;
}
