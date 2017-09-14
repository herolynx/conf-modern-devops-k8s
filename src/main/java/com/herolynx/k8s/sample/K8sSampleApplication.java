package com.herolynx.k8s.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class K8sSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(K8sSampleApplication.class, args);
	}

}
