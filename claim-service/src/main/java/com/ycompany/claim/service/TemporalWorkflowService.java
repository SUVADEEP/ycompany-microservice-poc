package com.ycompany.claim.service;

import com.ycompany.claim.workflow.ClaimWorkflowInterface;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class TemporalWorkflowService {
    
    @Value("${temporal.server.address:localhost:7233}")
    private String temporalServerAddress;
    
    private WorkflowServiceStubs service;
    private WorkflowClient client;

    @PostConstruct
    public void init() {
        service = WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalServerAddress)
                .build()
        );
        client = WorkflowClient.newInstance(service);
    }

    public void startClaimWorkflow(Long claimId) {
        ClaimWorkflowInterface workflow = client.newWorkflowStub(
            ClaimWorkflowInterface.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("claim-processing")
                .setWorkflowId("claim-workflow-" + claimId)
                .build()
        );
        
        // Start workflow asynchronously
        WorkflowClient.start(workflow::processClaim, claimId);
    }

    @PreDestroy
    public void cleanup() {
        if (service != null) {
            service.shutdown();
        }
    }
}

