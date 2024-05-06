package com.example.RestApi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabemployee")

public class Employee {
	@Id
	private String name;
	private String employee_name;
	private String first_name;
	private String last_name;
	private String gender;
	
	public Employee() {
		super();
	}

	public Employee(String name, String employee_name, String first_name, String last_name, String gender) {
		super();
		this.name = name;
		this.employee_name = employee_name;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmployee_name() {
		return employee_name;
	}

	public void setEmployee_name(String employee_name) {
		this.employee_name = employee_name;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Employee [name=" + name + ", employee_name=" + employee_name + ", first_name=" + first_name
				+ ", last_name=" + last_name + ", gender=" + gender + "]";
	}
	
	

}
