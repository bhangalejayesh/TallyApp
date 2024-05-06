package com.example.RestApi.services;

import java.util.List;
import java.util.Optional;

import com.example.RestApi.entities.Employee;

public interface EmployeeService {
	
	public List<Employee> getEmployee();

//	public String getEmployeeId(Employee employeeId);

	public Optional<Employee> getEmployeeId(String name);

	public List<Employee> getEmployeeId(String name, String gender);

//	String getEmployeeId();
}
