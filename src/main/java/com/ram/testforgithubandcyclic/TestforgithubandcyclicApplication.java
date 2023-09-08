package com.ram.testforgithubandcyclic;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@OpenAPIDefinition(
		info = @Info(
				title = "MyBlogs API",
				version = "1.0.0",
				description = "It's an API for Blogs website",
				termsOfService = "https://portfolio-ramkumargurav.vercel.app/",
				contact = @Contact(
						name = "Ramkumar Gurav",
						email = "ramkumarsgurav@gmail.com"
				),
				license = @License(
						name = "license",
						url = "https://portfolio-ramkumargurav.vercel.app/"

				)
		)
)
public class TestforgithubandcyclicApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestforgithubandcyclicApplication.class, args);
	}

}
