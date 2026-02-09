package com.example.security.config;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.security.service.JWTService;
import com.example.security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JWTService jwtService;
	
	private final UserService userService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail; 
		
		if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return ;
		}
		
		jwt = authHeader.substring(7);
		System.out.println("Extracted JWT: " + jwt);
		userEmail =  jwtService.extractUserName(jwt);
		
		if(StringUtils.hasText(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
			
			if(jwtService.isTokenValid(jwt, userDetails)) {
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				System.out.println("Authorities: " + userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(token);;
				
			}
		}
		
		filterChain.doFilter(request, response);
	}

}
