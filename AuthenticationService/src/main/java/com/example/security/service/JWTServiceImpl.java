package com.example.security.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.security.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl  implements JWTService {

	@Override
	public String generateToken(User userDetails) {
		
		Map<String, Object> claims = new HashMap<>();
		System.out.println(userDetails.getRole());
		
		claims.put("role", userDetails.getRole());
		claims.put("userId", userDetails.getId()); 
		
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() +1000 * 60 *24 ))	//24 min
				.signWith(getSignKey() , SignatureAlgorithm.HS256)
				.compact();
 
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private Key getSignKey() {
		String secret = "pX9Na8YT2Y+2KC7YjKqZgQ9xLP2V/3tQsfiN2N0FjsI=";
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	private <T> T extractClaim(String token , Function<Claims , T> claimResolvers) {
		final Claims claims = extractAllClaims(token);
		return claimResolvers.apply(claims);
	}


	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}


	public boolean isTokenValid(String token , UserDetails userDetails) {
		final String username = extractUserName(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

	}

	private boolean isTokenExpired( String token) {
		return  extractClaim(token, Claims::getExpiration).before(new Date());
	}

	@Override
	public String generateRefreshToken(Map<String, Object> extraClass, UserDetails userDetails) {
		// TODO Auto-generated method stub
		return null;
	}


}
