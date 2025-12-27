package com.example.CampusAccessManager.service;

import com.example.CampusAccessManager.HostRequest;
import com.example.CampusAccessManager.Role;
import com.example.CampusAccessManager.User;
import com.example.CampusAccessManager.dto.HostRequestDTO;
import com.example.CampusAccessManager.repository.HostRequestRepository;
import com.example.CampusAccessManager.repository.RoleRepository;
import com.example.CampusAccessManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HostApprovalService {

    @Autowired
    private HostRequestRepository hostRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Get all pending host requests
    public List<HostRequestDTO> getPendingRequests() {
        List<HostRequest> requests = hostRequestRepository.findByStatus("PENDING");
        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get all host requests (for admin to see history)
    public List<HostRequestDTO> getAllRequests() {
        List<HostRequest> requests = hostRequestRepository.findAll();
        return requests.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Add this method to HostApprovalService.java
    public List<HostRequest> getAllHostRequests() {
        return hostRequestRepository.findAll();
    }

    // Approve host request
    public String approveHostRequest(Long requestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth.getName();

        HostRequest request = hostRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        if (!request.getStatus().equals("PENDING")) {
            return "Request already " + request.getStatus().toLowerCase();
        }

        // Create user with HOST role
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Already encrypted

        Role hostRole = roleRepository.findByName("HOST")
                .orElseThrow(() -> new RuntimeException("HOST role not found!"));

        Set<Role> roles = new HashSet<>();
        roles.add(hostRole);
        user.setRoles(roles);

        userRepository.save(user);

        // Update request status
        request.setStatus("APPROVED");
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminUsername);
        hostRequestRepository.save(request);

        return "Host request approved! User can now login as HOST.";
    }

    // Reject host request
    public String rejectHostRequest(Long requestId, String reason) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth.getName();

        HostRequest request = hostRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        if (!request.getStatus().equals("PENDING")) {
            return "Request already " + request.getStatus().toLowerCase();
        }

        request.setStatus("REJECTED");
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(adminUsername);
        request.setRejectionReason(reason);
        hostRequestRepository.save(request);

        return "Host request rejected.";
    }

    private HostRequestDTO convertToDTO(HostRequest request) {
        HostRequestDTO dto = new HostRequestDTO();
        dto.setId(request.getId());
        dto.setUsername(request.getUsername());
        dto.setEmail(request.getEmail());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setReviewedAt(request.getReviewedAt());
        dto.setReviewedBy(request.getReviewedBy());
        dto.setRejectionReason(request.getRejectionReason());
        return dto;
    }
}
