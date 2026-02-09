package com.example.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.security.entity.User;

public interface JWTService {
	
	String generateToken(User userDetails);
	public String extractUserName(String token);
	public String extractRole(String token);
	public boolean isTokenValid(String token , UserDetails userDetails);
	public String generateRefreshToken(Map<String , Object> extraClass , UserDetails userDetails);

}
