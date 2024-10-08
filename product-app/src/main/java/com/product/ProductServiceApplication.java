package com.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableWebFlux
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ProductServiceApplication.class);
        application.addListeners(new ApplicationPidFileWriter("product-service.pid"));
        application.run(args);
    }
}
