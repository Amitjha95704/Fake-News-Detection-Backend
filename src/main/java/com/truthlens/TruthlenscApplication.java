package com.truthlens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TruthlenscApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(TruthlenscApplication.class, args);
	}

}
