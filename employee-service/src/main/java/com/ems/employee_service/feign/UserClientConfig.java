package com.ems.employee_service.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class UserClientConfig {
	
	@Bean
	public RequestInterceptor requestInterceptor() {
		return template->{
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if(attrs !=null) {
                HttpServletRequest request = attrs.getRequest();

                String authHeader = request.getHeader("Authorization");
                if(authHeader !=null) {
                	template.header("Authorization", authHeader);
                }
			}
		};
		
	}
}
