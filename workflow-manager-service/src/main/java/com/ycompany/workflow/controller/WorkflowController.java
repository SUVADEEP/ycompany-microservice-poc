package com.ycompany.workflow.controller;

import com.ycompany.common.dto.ApprovalRequestDTO;
import com.ycompany.common.dto.ClaimDTO;
import com.ycompany.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow")
@CrossOrigin(origins = "*")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/claims/{id}")
    public ResponseEntity<ClaimDTO> getClaimDetails(@PathVariable Long id) {
        ClaimDTO claim = workflowService.getClaimDetails(id);
        return ResponseEntity.ok(claim);
    }

    @PostMapping("/approve")
    public ResponseEntity<ClaimDTO> approveClaim(@Valid @RequestBody ApprovalRequestDTO approvalRequest) {
        ClaimDTO updatedClaim = workflowService.approveClaim(approvalRequest);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/claims/{claimId}/assign")
    public ResponseEntity<ClaimDTO> assignSupervisor(
            @PathVariable Long claimId, 
            @RequestParam String supervisorId) {
        ClaimDTO claim = workflowService.assignSupervisor(claimId, supervisorId);
        return ResponseEntity.ok(claim);
    }
}

