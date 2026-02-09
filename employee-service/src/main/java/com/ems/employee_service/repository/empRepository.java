package com.ems.employee_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ems.employee_service.entity.Employee;

@Repository
public interface empRepository extends JpaRepository<Employee, Long>{
	
	Optional<Employee> findByUserId(Integer userId);
	boolean existsByEmail(String email);
}
