package com.example.CampusAccessManager;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "host_requests")
public class HostRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password; 
    
    @Column(nullable = false)
    private String status; 
    
    @Column(nullable = false)
    private LocalDateTime requestedAt;
    
    private LocalDateTime reviewedAt;
    
    private String reviewedBy; 
    
    private String rejectionReason;
}