package com.ycompany.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApprovalRequestDTO {
    @NotNull(message = "Claim ID is required")
    private Long claimId;
    
    @NotBlank(message = "Supervisor ID is required")
    private String supervisorId;
    
    @NotBlank(message = "Decision is required")
    private String decision; // APPROVED, REJECTED
    
    private String comments;

    // Getters and Setters
    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

