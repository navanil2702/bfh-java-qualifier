package com.example.bfh;

import com.example.bfh.service.QualifierService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BfhApplication implements CommandLineRunner {

    private final ApplicationContext context;

    public BfhApplication(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(BfhApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // Fetch QualifierService from context manually to avoid circular reference
        QualifierService qualifierService = context.getBean(QualifierService.class);
        qualifierService.execute();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
