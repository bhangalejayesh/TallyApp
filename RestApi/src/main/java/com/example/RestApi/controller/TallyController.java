package com.example.RestApi.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class TallyController {
	
	 @GetMapping("/tally") 
	 public String helloWorld() { 
		  return "this tally page"; 
		  }

}
