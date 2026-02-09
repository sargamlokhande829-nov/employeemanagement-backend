package com.example.security.dto;

import com.example.security.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequest {

	 @NotBlank(message = "First name cannot be empty")
	 private String firstname;

	 @NotBlank(message = "Last name cannot be empty")
	 private String lastname;

	 @NotBlank(message = "Email cannot be empty")
	 private String email;

	 @NotBlank(message = "Password cannot be empty")
	 private String password;
	 
	 @NotNull(message = "Role is required")
	 private Role role;
	
}
