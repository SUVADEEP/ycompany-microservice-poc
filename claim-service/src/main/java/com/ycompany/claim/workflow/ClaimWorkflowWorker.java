package com.ycompany.claim.workflow;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class ClaimWorkflowWorker {
    
    @Value("${temporal.server.address:localhost:7233}")
    private String temporalServerAddress;
    
    private WorkflowServiceStubs service;
    private WorkflowClient client;
    private WorkerFactory factory;
    private Worker worker;

    @PostConstruct
    public void startWorker() {
        service = WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalServerAddress)
                .build()
        );
        client = WorkflowClient.newInstance(service);
        factory = WorkerFactory.newInstance(client);
        worker = factory.newWorker("claim-processing");
        
        worker.registerWorkflowImplementationTypes(
            com.ycompany.claim.workflow.ClaimWorkflowImpl.class
        );
        factory.start();
        
        System.out.println("Claim Workflow Worker started");
    }

    @PreDestroy
    public void shutdown() {
        if (factory != null) {
            factory.shutdown();
        }
        if (service != null) {
            service.shutdown();
        }
    }
}

