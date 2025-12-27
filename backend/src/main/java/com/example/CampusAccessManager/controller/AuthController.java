package com.example.CampusAccessManager.controller;

import com.example.CampusAccessManager.dto.LoginRequest;
import com.example.CampusAccessManager.dto.RegisterRequest;
import com.example.CampusAccessManager.dto.AuthResponse;
import com.example.CampusAccessManager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Login attempt for user: " + loginRequest.getUsername());
            AuthResponse response = authService.login(loginRequest);
            System.out.println("✅ Login successful for: " + loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed: " + e.getMessage());
        }
    }

  
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("📝 Registration attempt:");
            System.out.println("   Username: " + registerRequest.getUsername());
            System.out.println("   Email: " + registerRequest.getEmail());
            System.out.println("   Role: " + registerRequest.getRole());
            
            String message = authService.register(registerRequest);
            
            System.out.println("✅ Registration successful: " + message);
            return ResponseEntity.ok(message);
            
        } catch (Exception e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("✅ Auth API test endpoint hit");
        return ResponseEntity.ok("Auth API is working!");
    }
}