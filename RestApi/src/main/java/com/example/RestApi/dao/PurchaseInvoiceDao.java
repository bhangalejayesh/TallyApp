package com.example.RestApi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RestApi.entities.PurchaseInvoice;

public interface PurchaseInvoiceDao extends JpaRepository<PurchaseInvoice, String> {

}
