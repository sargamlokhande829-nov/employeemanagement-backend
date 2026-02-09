package com.ems.employee_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
public class Employee {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotBlank(message = "Name is required")
    private String name;
	
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;
    
    @NotNull
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Reporting Manager is required")
    private String reportingManager;
    
    // We will store just userId to link to Security Service User
    private Integer userId;
}
