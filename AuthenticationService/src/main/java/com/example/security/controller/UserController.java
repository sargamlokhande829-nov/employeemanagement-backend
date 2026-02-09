package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
    private UserRepository userRepository;
	
	@GetMapping("/email/{email}")	//pass-token 
	public ResponseEntity<User> getUserByEmail(@PathVariable String email){
		logger.info("Request to get user by email: {}", email);
		
		User user = userRepository.findByEmail(email);
		if(user==null) {
			 logger.warn("User not found for email: {}", email);
			return ResponseEntity.notFound().build();
		}
		 logger.info("User found: {}", user.getEmail());
		return ResponseEntity.ok(user);
	}
	
	@GetMapping("/id/{id}")		//optional
	public ResponseEntity<User> getUserById(@PathVariable Integer id){
		 logger.info("Request to get user by id: {}", id);
		return userRepository.findById(id)
				.map(user -> {
	                logger.info("User found: {}", user.getEmail());
	                return ResponseEntity.ok(user);
	            })
	            .orElseGet(() -> {
	                logger.warn("User not found for id: {}", id);
	                return ResponseEntity.notFound().build();
	            });
	}
}
