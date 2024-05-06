package com.example.RestApi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.RestApi.entities.Employee;
import com.example.RestApi.services.EmployeeService;

@RestController
@CrossOrigin
//@RequestMapping("/api/method/erpnext.ratc.print.pdf")
public class MyController {
	@Autowired
	private EmployeeService employeeService;
	
	
//	  @PostMapping("/home") public String home(@RequestBody Employee employeeId) { 
//		  return employeeService.getEmployeeId(employeeId); 
//		  }
	 @GetMapping("/home") 
	 public String helloWorld() { 
		  return "hello world"; 
		  }

	
	 @GetMapping("/employee/{id}") 
	 public Optional<Employee> getEmployeeId(@PathVariable("id") String name) { 
		  return employeeService.getEmployeeId(name); 
		  }
	 
	
	@GetMapping("/employee12")
	 public List<Employee> getEmployee() {
		return this.employeeService.getEmployee();
	}
	
	@GetMapping("/employee") 
	 public List<Employee> getEmployeePara(@RequestParam("id") String name,@RequestParam("gender") String gender) { 
		  return employeeService.getEmployeeId(name,gender); 
		  }
	
//	public void mapData() {
//		Map<String, Integer> map = new HashMap<>(); 
//		
//		map.put("vishal", 10);
//		map.put("Raju", 10);
//		
//		for (Map.Entry<String, Integer> e : map.entrySet()) {
//			System.out.println(e.getKey()+" value "+e.getValue());
//		}
//	}

	

}
