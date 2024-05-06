package com.example.RestApi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RestApi.dao.EmployeeDao;
import com.example.RestApi.entities.Employee;

@Service
public class EmployeeServiceImp implements EmployeeService {
	
//	List<Employee> list;
	@Autowired
	private EmployeeDao employeeDao;
	
	public EmployeeServiceImp(){
//		list=new ArrayList<>();
//		list.add(new Employee("HRE-EMP_01","jay shah","jay","shah","male"));
		
	}
	
	@Override
	public List<Employee> getEmployee() {
		// TODO Auto-generated method stub
		return employeeDao.findAll();
	}
	
	@Override
	public Optional<Employee> getEmployeeId(String name) {
		// TODO Auto-generated method stub
		return employeeDao.findById(name);
	}

	@Override
	public List<Employee> getEmployeeId(String name, String gender) {
		
		return employeeDao.findByNameAndGender(name, gender);
	}

}
