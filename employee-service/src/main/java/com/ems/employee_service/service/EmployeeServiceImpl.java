package com.ems.employee_service.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.entity.User;
import com.ems.employee_service.feign.UserClient;
import com.ems.employee_service.repository.empRepository;

import feign.FeignException;

@Service
public class EmployeeServiceImpl {
	
	@Autowired
	private UserClient userClient;
	@Autowired
	private empRepository empRepository;
	
		// ADD EMPLOYEE
		public Employee saveEmployee(Employee emp) {
			if(emp.getUserId()==null || emp.getEmail()==null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId must be provided when adding an employee");
			}
			
			//fetch user details from security-service via feign
			 User userFromSecurity;
			    try {
			        userFromSecurity = userClient.getUserById(emp.getUserId());
			    } catch (FeignException.NotFound e) {
			        // Handle 404 from Security Service
			        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist in Security Service");
			    } catch (FeignException e) {
			        // Handle other Feign errors
			        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching user from Security Service");
			    }
			
			emp.setEmail(userFromSecurity.getEmail());
			emp.setName(userFromSecurity.getFirstname()+" "+userFromSecurity.getLastname());
			
			System.out.println("saving the object");
			
			return empRepository.save(emp);
		}
	
		//UPDATE EMPLOYEE
		public Employee updateEmployee(Long id, Employee updatedEmp) {
			Employee existingEmp = empRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
			
			//user id linked to security-service
			updatedEmp.setUserId(existingEmp.getUserId());
			
			User userFromSecurity = userClient.getUserById(existingEmp.getUserId());
			if(userFromSecurity!=null) {		//name and email here is overwritten for security purpose
				updatedEmp.setEmail(userFromSecurity.getEmail());
				updatedEmp.setName(userFromSecurity.getFirstname()+" "+userFromSecurity.getLastname());
			}
			updatedEmp.setId(id);
			return empRepository.save(updatedEmp);
		}
		
		//DELETE EMPLOYEE
		public void deleteEmployee(Long id) {
			if(!empRepository.existsById(id)) {
				throw new RuntimeException("Employee not found with id: " + id);
			}
			empRepository.deleteById(id);
		}
		
		//GET ALL EMPLOYEES
		public List<Employee> getAllEmployees(){
			return empRepository.findAll();
		}
		
		//BGET EMPLOYEE BY ID
		public Employee getEmployeeById(Long id, Integer loggedInUserId, String role) {
			Employee employee = empRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee not found with id: "+id));
			
			if ("ADMIN".equals(role) || "HR".equals(role)) {
		        return employee;
		    }

		    if ("EMPLOYEE".equals(role)
		            && employee.getUserId() != null
		            && employee.getUserId().equals(loggedInUserId)) {
		        return employee;
		    }

		    throw new ResponseStatusException(
		            HttpStatus.FORBIDDEN,
		            "You are not allowed to access other employee details"
		    );
		}
		
		//GET EMPLOYEE PROFILE BY EMAIL
		public Employee getEmployeeProfile(String email) {
			
			// Fetch user by email from Security Service
			User userFromSecurity;
		    try {
		        userFromSecurity = userClient.getUserByEmail(email);
		    } catch (Exception ex) {
		        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User does not exist in Security Service");
		    }

		    if (userFromSecurity == null) {
		        throw new ResponseStatusException( HttpStatus.NOT_FOUND,"User does not exist in Security Service");
		    }
		    
		   // Fetch employee by userId
		    return empRepository.findByUserId(userFromSecurity.getId())
		            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee profile not found"));
		}
				
		//GET EMPLOYEE BY USERID
		public Employee getEmployeeByUserId(Integer id) {
			if (id == null) {
		        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Logged-in user id is missing");
		    }

		    return empRepository.findByUserId(id)
		            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee profile not found for logged-in user"));
		}
		
		// CHECK IF EMPLOYEE EXISTS FOR USER
		public boolean isEmployeePresent(String email) {
		    if (email == null) {
		        throw new ResponseStatusException(
		            HttpStatus.BAD_REQUEST,
		            "User id is required"
		        );
		    }

		    return empRepository.existsByEmail(email);
		}
	
}
