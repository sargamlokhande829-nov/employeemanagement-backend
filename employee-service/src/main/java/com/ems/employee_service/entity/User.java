package com.ems.employee_service.entity;

import lombok.Data;

@Data
public class User {
	
	 private Integer id;
	 private String firstname;
	 private String lastname;
	 private String email;
	 private String role;
}
