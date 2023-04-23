package ru.mirea.novelland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class NovelLandApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelLandApplication.class, args);
	}

}
