package com.employee_management_system.api_gateway.config;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {
	
	private final String SECRET_KEY="pX9Na8YT2Y+2KC7YjKqZgQ9xLP2V/3tQsfiN2N0FjsI=";
	
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public boolean isTokenValid(String token) {
		try {
			extractClaims(token);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	public String extractRole(String token) {
		return extractClaims(token).get("role",String.class);
	}
	
	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}
	
	public Integer extractUserId(String token) {
	    return extractClaims(token).get("userId", Integer.class);
	}
}
