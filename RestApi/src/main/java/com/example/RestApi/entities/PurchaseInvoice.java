package com.example.RestApi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabPurchase Invoice")
public class PurchaseInvoice {
	
	@Id
	private String name;
	private String customer;
	
	private String gst_category;
	
	private String Customer_address;
	
	@ManyToOne
	private Customer customers;
	
	@ManyToOne
	private Supplier supplier;
	

	@ManyToOne
//    @JoinColumn(name = "customer_address", referencedColumnName = "name")
    private Address address;


	public String getCustomer() {
		return customer;
	}


	public void setCustomer(String customer) {
		this.customer = customer;
	}


	public String getGst_category() {
		return gst_category;
	}


	public void setGst_category(String gst_category) {
		this.gst_category = gst_category;
	}


	public String getCustomer_address() {
		return Customer_address;
	}


	public void setCustomer_address(String customer_address) {
		Customer_address = customer_address;
	}


	public Customer getCustomers() {
		return customers;
	}


	public void setCustomers(Customer customers) {
		this.customers = customers;
	}


	public Supplier getSupplier() {
		return supplier;
	}


	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}


	public Address getAddress() {
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
	}

}
