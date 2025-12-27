package com.example.CampusAccessManager.controller;

import com.example.CampusAccessManager.User;
import com.example.CampusAccessManager.dto.EmailRequest;
import com.example.CampusAccessManager.service.EmailService;
import com.example.CampusAccessManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/host")
public class HostController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   
    @PostMapping("/message/all")
    public ResponseEntity<String> sendToAllStudents(@RequestBody EmailRequest request) {
        try {
            String result = emailService.sendEmailToAllStudents(
                request.getSubject(),
                request.getMessage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    /**
     * SEND MESSAGE TO ONE SPECIFIC STUDENT
     */
    @PostMapping("/message/one")
    public ResponseEntity<String> sendToOneStudent(@RequestBody EmailRequest request) {
        try {
            String result = emailService.sendEmailToOneStudent(
                request.getUsername(),
                request.getSubject(),
                request.getMessage()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }

    /**
     * GET ALL STUDENTS (HOST CAN VIEW STUDENTS)
     */
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

   
    @PostMapping("/users/change-password")
    public ResponseEntity<String> changeStudentPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String newPassword = request.get("newPassword");
            
            if (username == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
          
            boolean isStudent = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("STUDENT"));
            
            if (!isStudent) {
                return ResponseEntity.badRequest().body("User is not a student!");
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Password changed successfully for " + username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed: " + e.getMessage());
        }
    }
}