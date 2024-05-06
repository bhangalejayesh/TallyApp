package com.example.RestApi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class RestApiApplication extends SpringBootServletInitializer{
	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
//		SpringApplication.run(args);
//		mapData();
	}
	
	

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(RestApiApplication.class);
	}



//	private static void mapData() {
//		Map<String, Integer> map = new HashMap<>(); 
//		
//		map.put("vishal", 10);
//		map.put("Raju", 10);
//		
//		for (Map.Entry<String, Integer> e : map.entrySet()) {
//			System.out.println(e.getKey()+" value "+e.getValue());
//		}
//	}

	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Spring boot started");
		
	}

}
