package com.ems.employee_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ems.employee_service.entity.User;

@FeignClient(name="security-service", url="http://localhost:9093", configuration = UserClientConfig.class)
public interface UserClient {
	
	@GetMapping("/api/v1/users/email/{email}")
	User getUserByEmail(@PathVariable("email") String email);
	
	@GetMapping("/api/v1/users/id/{id}")
	User getUserById(@PathVariable("id") Integer id);
}
