package com.example.CampusAccessManager.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HostRequestDTO {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String rejectionReason;
}