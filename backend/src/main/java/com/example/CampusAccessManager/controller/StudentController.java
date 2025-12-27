package com.example.CampusAccessManager.controller;

import com.example.CampusAccessManager.dto.MessageDTO;
import com.example.CampusAccessManager.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getMyMessages() {
        try {
            List<MessageDTO> messages = studentService.getMyMessages();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.out.println("Error getting messages: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        try {
            String result = studentService.deleteMessage(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error deleting message: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to delete message: " + e.getMessage());
        }
    }
}