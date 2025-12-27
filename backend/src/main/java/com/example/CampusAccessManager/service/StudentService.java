package com.example.CampusAccessManager.service;

import com.example.CampusAccessManager.*;
import com.example.CampusAccessManager.dto.MessageDTO;
import com.example.CampusAccessManager.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private MessageRepository messageRepository;

    public List<MessageDTO> getMyMessages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        System.out.println("Getting messages for student: " + username);

        // Get all broadcast messages
        List<Message> allMessages = messageRepository.findByRecipientTypeOrderBySentAtDesc("ALL");
        System.out.println("Found " + allMessages.size() + " broadcast messages");
        
        // Get messages specifically for this student
        List<Message> specificMessages = messageRepository.findByRecipientUsernameOrderBySentAtDesc(username);
        System.out.println("Found " + specificMessages.size() + " specific messages");

        // Combine both lists
        List<Message> combined = new ArrayList<>();
        combined.addAll(allMessages);
        combined.addAll(specificMessages);

        // Remove duplicates based on message ID and sort by sent date (newest first)
        Map<Long, Message> uniqueMessages = new LinkedHashMap<>();
        for (Message msg : combined) {
            uniqueMessages.put(msg.getId(), msg);
        }

        List<Message> deduplicated = new ArrayList<>(uniqueMessages.values());
        deduplicated.sort((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()));

        System.out.println("Returning " + deduplicated.size() + " unique messages");

        return deduplicated.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public String deleteMessage(Long messageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        System.out.println("Student " + username + " attempting to delete message " + messageId);

        Optional<Message> messageOpt = messageRepository.findById(messageId);
        
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message not found!");
        }

        Message message = messageOpt.get();

        // Check if the student is allowed to delete this message
        // Students can delete broadcast messages or messages specifically sent to them
        boolean canDelete = message.getRecipientType().equals("ALL") || 
                           (message.getRecipientType().equals("SPECIFIC") && 
                            message.getRecipientUsername() != null &&
                            message.getRecipientUsername().equals(username));

        if (!canDelete) {
            System.out.println("User " + username + " not authorized to delete message " + messageId);
            throw new RuntimeException("You are not authorized to delete this message!");
        }

        messageRepository.deleteById(messageId);
        System.out.println("Message " + messageId + " deleted successfully by " + username);
        return "Message deleted successfully!";
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSubject(message.getSubject());
        dto.setMessage(message.getMessage());
        dto.setSenderUsername(message.getSenderUsername());
        dto.setSentAt(message.getSentAt());
        dto.setRecipientType(message.getRecipientType());
        return dto;
    }
}