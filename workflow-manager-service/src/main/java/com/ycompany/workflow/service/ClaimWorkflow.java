package com.ycompany.workflow.service;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ClaimWorkflow {
    @WorkflowMethod
    void processClaim(Long claimId);
    
    @SignalMethod
    void approveClaim(Long claimId, String supervisorId, String decision, String comments);
}

