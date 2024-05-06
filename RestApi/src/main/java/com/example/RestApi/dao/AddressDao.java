package com.example.RestApi.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RestApi.entities.Address;

public interface AddressDao extends JpaRepository<Address, String> {

}
