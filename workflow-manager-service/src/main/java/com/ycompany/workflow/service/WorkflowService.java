package com.ycompany.workflow.service;

import com.ycompany.common.dto.ApprovalRequestDTO;
import com.ycompany.common.dto.ClaimDTO;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class WorkflowService {
    
    @Value("${claim.service.url:http://localhost:8081}")
    private String claimServiceUrl;
    
    @Value("${temporal.server.address:localhost:7233}")
    private String temporalServerAddress;
    
    private WorkflowServiceStubs service;
    private WorkflowClient client;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        service = WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalServerAddress)
                .build()
        );
        client = WorkflowClient.newInstance(service);
        webClient = WebClient.builder()
                .baseUrl(claimServiceUrl)
                .build();
    }

    public ClaimDTO getClaimDetails(Long claimId) {
        return webClient.get()
                .uri("/claims/{id}", claimId)
                .retrieve()
                .bodyToMono(ClaimDTO.class)
                .block();
    }

    public ClaimDTO approveClaim(ApprovalRequestDTO approvalRequest) {
        // Signal the Temporal workflow
        ClaimWorkflow workflow = client.newWorkflowStub(
            ClaimWorkflow.class,
            "claim-workflow-" + approvalRequest.getClaimId()
        );
        
        workflow.approveClaim(approvalRequest.getClaimId(), 
                            approvalRequest.getSupervisorId(), 
                            approvalRequest.getDecision(),
                            approvalRequest.getComments());
        
        // Update claim status via Claim Service
        String status = "APPROVED".equals(approvalRequest.getDecision()) ? "APPROVED" : "REJECTED";
        
        return webClient.patch()
                .uri("/claims/{id}/status?status={status}", 
                     approvalRequest.getClaimId(), status)
                .retrieve()
                .bodyToMono(ClaimDTO.class)
                .block();
    }

    public ClaimDTO assignSupervisor(Long claimId, String supervisorId) {
        ClaimDTO claim = getClaimDetails(claimId);
        claim.setSupervisorId(supervisorId);
        
        return webClient.put()
                .uri("/claims/{id}", claimId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(claim)
                .retrieve()
                .bodyToMono(ClaimDTO.class)
                .block();
    }

    @PreDestroy
    public void cleanup() {
        if (service != null) {
            service.shutdown();
        }
    }
}

