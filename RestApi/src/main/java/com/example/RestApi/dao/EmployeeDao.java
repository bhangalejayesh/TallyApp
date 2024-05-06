package com.example.RestApi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RestApi.entities.Employee;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, String> {
	
	List<Employee> findByNameAndGender(String name, String gender);

}
