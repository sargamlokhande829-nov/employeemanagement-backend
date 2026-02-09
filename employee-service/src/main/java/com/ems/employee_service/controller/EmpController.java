package com.ems.employee_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.service.EmployeeServiceImpl;

import jakarta.validation.Valid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/employee")
//@CrossOrigin(origins = "http://localhost:3000") // Allow frontend origin
public class EmpController {
	
	private static final Logger logger = LoggerFactory.getLogger(EmpController.class);

    @Autowired
    private EmployeeServiceImpl employeeService;
    
    @GetMapping("/test")
    public ResponseEntity<String> test(){
    	logger.info("Employee controller is up.");
    	return ResponseEntity.ok("Employee service is running.");
    }
    
    //ADMIN ONLY
    @PostMapping("/add")
    public ResponseEntity<Employee> saveEmployee(@Validated @RequestBody Employee emp){
    	 logger.info("Adding new employee for userId: {}", emp.getUserId());
    	Employee savedEmp = employeeService.saveEmployee(emp);
    	return new ResponseEntity<>(savedEmp, HttpStatus.CREATED);
    }
    
    //ADMIN ONLY
    @PutMapping("/update/{id}")	//employee-id
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        logger.info("Updating employee with id: {}", id);
        Employee updatedEmp = employeeService.updateEmployee(id, employee);
        updatedEmp.setDepartment(employee.getDepartment());
        updatedEmp.setPhone(employee.getPhone());
        updatedEmp.setReportingManager(employee.getReportingManager());
        
        updatedEmp = employeeService.saveEmployee(updatedEmp);	//updated needs to be overwrite
        return ResponseEntity.ok(updatedEmp);
    }
    
    //ADMIN ONLY
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id){
    	logger.info("Deleting employee with id: {}", id);
    	employeeService.deleteEmployee(id);
    	return ResponseEntity.ok("Employee deleted successfully.");
    }
    
    //ADMIN & HR
    @GetMapping("/getAll")
    public ResponseEntity<List<Employee>> getAllEmployees(){
    	logger.info("Fetching all employees.");
    	return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    
    //EMPLOYEE ONLY
    @GetMapping("/getEmployeeById/{id}")	//gets the details of own 
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id, @RequestHeader("X-User-Id") Integer loggedInUserId, @RequestHeader("X-User-Roles") String role){
    	logger.info("Fetching employee by id: {}", id);
    	Employee employee =
                employeeService.getEmployeeById(id, loggedInUserId, role);

        return ResponseEntity.ok(employee);
    }
    
    //EMPLOYEE ONLY
    @GetMapping("/profile")			//gets the details of own 
    public ResponseEntity<Employee> getEmployeeProfile(@RequestHeader("X-User-Name") String loggedInEmail) {
        logger.info("Fetching employee profile for email: {}", loggedInEmail);
        return ResponseEntity.ok(employeeService.getEmployeeProfile(loggedInEmail));
    }
    
    //EMPLOYEE ONLY
    @GetMapping("/user")
    public ResponseEntity<Employee> getEmployeeByUserId(@RequestHeader("X-User-Id") Integer userId) {
    	logger.info("Fetching employee by userId: {}", userId);
    	Employee emp = employeeService.getEmployeeByUserId(userId);
    	return ResponseEntity.ok(emp);
    }
    
    @GetMapping("/existsByEmail/{email}")
    public ResponseEntity<Boolean> isEmployeePresent(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.isEmployeePresent(email));
    }
    
}
