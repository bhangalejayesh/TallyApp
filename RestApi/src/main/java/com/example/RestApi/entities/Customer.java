package com.example.RestApi.entities;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabCustomer")
public class Customer {
	 @Id
	private String name;
	
	private String gstin;
	
	@OneToMany(mappedBy = "customers", fetch = FetchType.LAZY)
    private Set<SalesInvoice> sales;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public Set<SalesInvoice> getSales() {
		return sales;
	}

	public void setSales(Set<SalesInvoice> sales) {
		this.sales = sales;
	}
	
	

}
