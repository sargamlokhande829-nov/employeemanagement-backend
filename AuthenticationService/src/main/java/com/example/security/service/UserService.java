package com.example.security.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Component
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			
			public UserDetails loadUserByUsername(String username) {
				User user = userRepository.findByEmail(username);
				if (user == null) {
			        System.out.println("User not found!");
			        throw new RuntimeException("useranme not found: "+username);
			    }
			    System.out.println("Found user: " + user.getEmail());
				List<GrantedAuthority> authorities = List.of(
						new SimpleGrantedAuthority("ROLE_"+user.getRole())
						);
				return new org.springframework.security.core.userdetails.User(
						user.getEmail(), 
						user.getPassword(), 	 
						authorities);
			}
		};
	}
}
