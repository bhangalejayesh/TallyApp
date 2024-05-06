package com.example.RestApi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.RestApi.entities.Customer;

@Repository
public interface CustomerTallyDao extends JpaRepository<Customer, String> {

//	@Query(
//			"select distinct upper(party.name) name, tran.gst_category, '{pg}' as parent_account, party.gstin, address.state " 
//					"from tab{party} party " 
//					"inner join `tab{ttype} Invoice` tran on(tran.customer=party.name) " 
//					"inner join tabAddress address on(address.name=tran.{party}_address)\n";
//			)
//	List<Map<String, Object>> findCustomerData();
}
