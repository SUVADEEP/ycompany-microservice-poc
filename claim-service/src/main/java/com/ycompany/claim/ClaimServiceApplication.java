package com.ycompany.claim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.ycompany.claim", "com.ycompany.common"})
@EntityScan(basePackages = {"com.ycompany.claim.entity"})
@EnableJpaRepositories(basePackages = {"com.ycompany.claim.repository"})
public class ClaimServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimServiceApplication.class, args);
    }
}

