package com.example.CampusAccessManager.service;

import com.example.CampusAccessManager.*;
import com.example.CampusAccessManager.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired 
    private JavaMailSender mailSender;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired 
    private MessageRepository messageRepository;

    public String sendEmailToAllStudents(String subject, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = auth.getName();

        System.out.println("📧 Sending broadcast message from: " + senderUsername);

        // Get all student emails
        List<String> studentEmails = userRepository.findAll().stream()
            .filter(user -> user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("STUDENT")))
            .map(User::getEmail)
            .collect(Collectors.toList());

        if (studentEmails.isEmpty()) {
            return "No students found!";
        }

        System.out.println("📬 Found " + studentEmails.size() + " students");

        // Send emails
        int successCount = 0;
        for (String email : studentEmails) {
            try {
                sendEmail(email, subject, message);
                successCount++;
            } catch (Exception e) {
                System.out.println("❌ Failed to send to: " + email);
            }
        }

        // ✅ SAVE ONLY ONE MESSAGE RECORD FOR BROADCAST (not one per student)
        Message msg = new Message();
        msg.setSubject(subject);
        msg.setMessage(message);
        msg.setSenderUsername(senderUsername);
        msg.setSentAt(LocalDateTime.now());
        msg.setRecipientType("ALL");  // This indicates it's for all students
        msg.setRecipientUsername(null);  // No specific recipient
        
        messageRepository.save(msg);
        System.out.println("✅ Saved 1 broadcast message record");

        return "Email sent to " + successCount + " students out of " + studentEmails.size();
    }

    public String sendEmailToOneStudent(String username, String subject, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = auth.getName();

        System.out.println("📧 Sending direct message from " + senderUsername + " to " + username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "Student not found: " + username;
        }

        User user = userOpt.get();
        boolean isStudent = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals("STUDENT"));

        if (!isStudent) {
            return "User " + username + " is not a student!";
        }

        try {
            // Send the email
            sendEmail(user.getEmail(), subject, message);

            // Save message to database
            Message msg = new Message();
            msg.setSubject(subject);
            msg.setMessage(message);
            msg.setSenderUsername(senderUsername);
            msg.setSentAt(LocalDateTime.now());
            msg.setRecipientType("SPECIFIC");  // This indicates it's for a specific student
            msg.setRecipientUsername(username);  // Store the specific recipient
            
            messageRepository.save(msg);
            System.out.println("✅ Saved direct message to " + username);

            return "Email sent to " + username;
        } catch (Exception e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
            return "Failed to send email: " + e.getMessage();
        }
    }

    private void sendEmail(String to, String subject, String message) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, false);
        mailSender.send(mimeMessage);
    }
}