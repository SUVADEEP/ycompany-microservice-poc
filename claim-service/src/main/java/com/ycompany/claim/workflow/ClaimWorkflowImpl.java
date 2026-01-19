package com.ycompany.claim.workflow;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class ClaimWorkflowImpl implements ClaimWorkflowInterface {
    
    private static final Logger logger = Workflow.getLogger(ClaimWorkflowImpl.class);
    private String approvalDecision;
    private String supervisorId;
    private String comments;

    @Override
    public void processClaim(Long claimId) {
        logger.info("Starting claim processing workflow for claim ID: {}", claimId);
        
        // Wait for supervisor approval (this will be signaled from Workflow Manager Service)
        Workflow.await(Duration.ofDays(30), () -> approvalDecision != null);
        
        if (approvalDecision != null) {
            logger.info("Claim {} processed with decision: {} by supervisor: {}", 
                       claimId, approvalDecision, supervisorId);
        } else {
            logger.warn("Claim {} processing timed out waiting for approval", claimId);
        }
    }

    @Override
    public void approveClaim(Long claimId, String supervisorId, String decision, String comments) {
        logger.info("Received approval signal for claim {}: decision={}, supervisor={}", 
                   claimId, decision, supervisorId);
        this.approvalDecision = decision;
        this.supervisorId = supervisorId;
        this.comments = comments;
    }
}
