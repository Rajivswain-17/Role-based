package com.example.CampusAccessManager.controller;

import com.example.CampusAccessManager.HostRequest;
import com.example.CampusAccessManager.User;
import com.example.CampusAccessManager.service.HostApprovalService;
import com.example.CampusAccessManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private HostApprovalService hostApprovalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/host-requests")
    public ResponseEntity<List<HostRequest>> getAllHostRequests() {
        try {
            List<HostRequest> requests = hostApprovalService.getAllHostRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/host-requests/{id}/approve")
    public ResponseEntity<String> approveHostRequest(@PathVariable Long id) {
        try {
            String result = hostApprovalService.approveHostRequest(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    @PostMapping("/host-requests/{id}/reject")
    public ResponseEntity<String> rejectHostRequest(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : "No reason provided";
            String result = hostApprovalService.rejectHostRequest(id, reason);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    @GetMapping("/users/students")
    public ResponseEntity<List<Map<String, String>>> getAllStudents() {
        try {
            List<Map<String, String>> students = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("STUDENT")))
                .map(user -> {
                    Map<String, String> studentInfo = new HashMap<>();
                    studentInfo.put("id", user.getId().toString());
                    studentInfo.put("username", user.getUsername());
                    studentInfo.put("email", user.getEmail());
                    return studentInfo;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/hosts")
    public ResponseEntity<List<Map<String, String>>> getAllHosts() {
        try {
            List<Map<String, String>> hosts = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("HOST")))
                .map(user -> {
                    Map<String, String> hostInfo = new HashMap<>();
                    hostInfo.put("id", user.getId().toString());
                    hostInfo.put("username", user.getUsername());
                    hostInfo.put("email", user.getEmail());
                    return hostInfo;
                })
                .collect(Collectors.toList());
            return ResponseEntity.ok(hosts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/change-password")
    public ResponseEntity<String> changeUserPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String newPassword = request.get("newPassword");
            
            if (username == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Password changed successfully for " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }
}