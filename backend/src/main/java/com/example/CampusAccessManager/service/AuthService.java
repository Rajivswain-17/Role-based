package com.example.CampusAccessManager.service;

import com.example.CampusAccessManager.Role;
import com.example.CampusAccessManager.User;
import com.example.CampusAccessManager.HostRequest;
import com.example.CampusAccessManager.dto.AuthResponse;
import com.example.CampusAccessManager.dto.LoginRequest;
import com.example.CampusAccessManager.dto.RegisterRequest;
import com.example.CampusAccessManager.repository.RoleRepository;
import com.example.CampusAccessManager.repository.UserRepository;
import com.example.CampusAccessManager.repository.HostRequestRepository;
import com.example.CampusAccessManager.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HostRequestRepository hostRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        String token = jwtUtil.generateToken(authentication);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(token, request.getUsername(), roles);
    }

   
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        String roleName = request.getRole() != null ? request.getRole().toUpperCase() : "STUDENT";

        
//        if (roleName.equals("ADMIN")) {
//           
//            long adminCount = userRepository.findAll().stream()
//                .filter(user -> user.getRoles().stream()
//                    .anyMatch(role -> role.getName().equals("ADMIN")))
//                .count();
//            
//            if (adminCount > 0) {
//                throw new RuntimeException("Admin registration is closed. Only one admin is allowed!");
//            }
//        }

        // If registering as HOST, create a pending request instead
        if (roleName.equals("HOST")) {
            if (hostRequestRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Host request already submitted for this username!");
            }
            if (hostRequestRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Host request already submitted for this email!");
            }

            HostRequest hostRequest = new HostRequest();
            hostRequest.setUsername(request.getUsername());
            hostRequest.setEmail(request.getEmail());
            hostRequest.setPassword(passwordEncoder.encode(request.getPassword()));
            hostRequest.setStatus("PENDING");
            hostRequest.setRequestedAt(LocalDateTime.now());
            
            hostRequestRepository.save(hostRequest);
            
            return "Host registration request submitted! Waiting for admin approval.";
        }

        // For STUDENT and ADMIN, register directly
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully!";
    }
}