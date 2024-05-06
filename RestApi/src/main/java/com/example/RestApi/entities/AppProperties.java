package com.example.RestApi.entities;


public class AppProperties {
	private String url;
	private String username;
	private String password;
	private String outputFolder;
	
	public AppProperties() {
		super();
		this.url="jdbc:mariadb://localhost:3306/satpuda";
		this.username="root";
		this.password="jayesh";
//		this.outputFolder = "default/output/path";
		this.outputFolder = "D://JavaProject/RestApi";
		// TODO Auto-generated constructor stub 
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	
}
