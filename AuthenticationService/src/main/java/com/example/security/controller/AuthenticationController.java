package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.example.security.dto.JwtAuthenticationResponse;
import com.example.security.dto.SignInRequest;
import com.example.security.dto.SignUpRequest;
import com.example.security.entity.User;
import com.example.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")		//Allows frontend domain
public class AuthenticationController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired
	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<User> signup(@Valid @RequestBody SignUpRequest signUpRequest){
		logger.info("User signed up successfully: {}",signUpRequest.getEmail());
		return ResponseEntity.ok(authenticationService.signup(signUpRequest));
	}
	
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signin(@Valid @RequestBody SignInRequest signInRequest){
		logger.info("User signed in successfully: {}", signInRequest.getEmail());
		return ResponseEntity.ok(authenticationService.signin(signInRequest));
	}
	
	@GetMapping("/validate")
    public ResponseEntity<String> validate() {
    	logger.info("Validations are passed successfully.");
        return ResponseEntity.ok("Validation passed!");
    }
	
	
	
}
