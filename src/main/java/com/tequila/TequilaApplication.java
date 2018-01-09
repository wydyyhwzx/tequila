package com.tequila;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class TequilaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TequilaApplication.class, args);
	}
}
