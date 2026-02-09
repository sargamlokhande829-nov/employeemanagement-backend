package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.yaml.snakeyaml.comments.CommentLine;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class SecurityApplication  implements CommandLineRunner{
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityApplication.class);

	@Autowired
	private UserRepository userRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			logger.info("Checking if admin account exists..");
			
			User adminAccount = userRepository.findByRole(Role.ADMIN);
			
			if(adminAccount == null) {
				
				logger.info("Admin account not found. Creating default admin user...");
				User user = new User();
				
				user.setEmail("admin@gmail.com");
				user.setFirstname("admin");
				user.setLastname("admin");
				user.setRole(Role.ADMIN);
				user.setPassword(new BCryptPasswordEncoder().encode("admin"));
				logger.info("Admin account created successfully.");
				userRepository.save(user);
				
			}else {
		        logger.info("Admin account already exists.");
		    }
		}catch (Exception e) {
	        logger.error("Error occurred while setting up admin account: {}", e.getMessage(), e);
	    }
	}

}
