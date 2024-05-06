package com.example.RestApi.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataConfig {
	
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource=new HikariDataSource();
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;	
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
		}
	
}
