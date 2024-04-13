package com.springboot.mongodb.app.libros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class MsvcLibrosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcLibrosApplication.class, args);
	}

}
