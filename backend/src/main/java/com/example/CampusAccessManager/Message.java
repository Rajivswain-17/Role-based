package com.example.CampusAccessManager;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String senderUsername; 

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private String recipientType; 

    private String recipientUsername; 
}
