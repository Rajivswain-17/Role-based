package com.example.CampusAccessManager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.CampusAccessManager.HostRequest;

public interface HostRequestRepository extends JpaRepository<HostRequest, Long> {
	 List<HostRequest> findByStatus(String status);
	    boolean existsByUsername(String username);
	    boolean existsByEmail(String email);
	}
