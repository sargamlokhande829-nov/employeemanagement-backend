package com.example.security.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.security.dto.JwtAuthenticationResponse;
import com.example.security.dto.SignInRequest;
import com.example.security.dto.SignUpRequest;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder; 
	private final AuthenticationManager authenticationManager;
	private final JWTService jwtService;

	public User signup(SignUpRequest signUpRequest) {
		
		logger.info("Received signup request for email: {}", signUpRequest.getEmail());
		
		if(signUpRequest.getRole().equals(Role.ADMIN)) {
			logger.warn("Registration failed - An admin already exists. Attempted by '{}'", signUpRequest.getEmail());
    		throw new RuntimeException("Admin already exists, cannot register another admin.");
		}
		
		if(signUpRequest.getRole()==null) {
			logger.warn("Registration failed - No role provided for '{}'", signUpRequest.getEmail());
        	throw new RuntimeException("Role must be provided");
		}
		
		if(userRepository.findByEmail(signUpRequest.getEmail()) !=null) {
			logger.warn("Registration failed - Username '{}' already exists", signUpRequest.getEmail());
        	throw new RuntimeException("Email already exists.");
		}
		
		User user = new User();
		user.setEmail(signUpRequest.getEmail());
		user.setFirstname(signUpRequest.getFirstname());
		user.setLastname(signUpRequest.getLastname());
		user.setRole(signUpRequest.getRole());
		user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		
		logger.info("User signed up successfully: {}", signUpRequest.getEmail());
		
		return userRepository.save(user);
	}
	

	public JwtAuthenticationResponse signin(SignInRequest signinRequest) {
		
		logger.info("Received signin request for email: {}", signinRequest.getEmail());
		
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken( signinRequest.getEmail(), signinRequest.getPassword()));
		
		var user = userRepository.findByEmail(signinRequest.getEmail());
	    System.out.println(user +" inside service");

	    if (user == null) {
            logger.warn("Signin failed: No user found with email: {}", signinRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("User authenticated successfully: {}", signinRequest.getEmail());

	    
		var jwt = jwtService.generateToken(user);
		
		logger.info("JWT token generated for user: {}", signinRequest.getEmail());

		JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
		jwtAuthenticationResponse.setToken(jwt);
		
		return jwtAuthenticationResponse;
		
	}
}
