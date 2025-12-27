package com.example.CampusAccessManager.config;
import com.example.CampusAccessManager.Role;
import com.example.CampusAccessManager.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================");
        System.out.println("Starting Database Initialization");
        System.out.println("==================================");

        
        
        
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
            System.out.println("✓ Created ADMIN role");
        } else {
            System.out.println("✓ ADMIN role already exists");
        }

        
        
        
        if (roleRepository.findByName("HOST").isEmpty()) {
            Role hostRole = new Role();
            hostRole.setName("HOST");
            roleRepository.save(hostRole);
            System.out.println("✓ Created HOST role");
        } else {
            System.out.println("✓ HOST role already exists");
        }

        
        
        
        
        if (roleRepository.findByName("STUDENT").isEmpty()) {
            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            roleRepository.save(studentRole);
            System.out.println("✓ Created STUDENT role");
        } else {
            System.out.println("✓ STUDENT role already exists");
        }

        System.out.println("==================================");
        System.out.println("Database Initialization Complete!");
        System.out.println("==================================");
    }
}