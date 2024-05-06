package com.example.RestApi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RestApi.entities.SalesInvoice;

public interface SalesInvoiceDao extends JpaRepository<SalesInvoice, String> {

}
