package com.ycompany.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.ycompany.workflow", "com.ycompany.common"})
@ComponentScan(basePackages = {"com.ycompany.workflow", "com.ycompany.common"})
public class WorkflowManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowManagerApplication.class, args);
    }
}

