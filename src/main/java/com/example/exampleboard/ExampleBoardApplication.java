package com.example.exampleboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@ComponentScan(basePackages = "com.example.exampleboard")
public class ExampleBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleBoardApplication.class, args);
	}

}
