package com.thaliadeshalit.linkscraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LinkScraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkScraperApplication.class, args);
	}
}
