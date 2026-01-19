package com.ycompany.claim.controller;

import com.ycompany.claim.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/policies")
@CrossOrigin(origins = "*")
public class PolicyController {
    
    @Autowired
    private PolicyService policyService;
    
    /**
     * Generate a unique policy number for a customer
     * GET /policies/generate?customerId=CUST001
     * 
     * @param customerId The customer ID
     * @return A unique policy number
     */
    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generatePolicyNumber(
            @RequestParam("customerId") String customerId) {
        
        if (customerId == null || customerId.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Customer ID is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        String policyNumber = policyService.generateUniquePolicyNumber(customerId.trim());
        
        Map<String, String> response = new HashMap<>();
        response.put("policyNumber", policyNumber);
        response.put("customerId", customerId.trim());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if a policy number exists
     * GET /policies/check?policyNumber=POL-CUST001-20260119-1234
     * 
     * @param policyNumber The policy number to check
     * @return true if exists, false otherwise
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPolicyNumber(
            @RequestParam("policyNumber") String policyNumber) {
        
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Policy number is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        boolean exists = policyService.isPolicyNumberExists(policyNumber.trim());
        
        Map<String, Object> response = new HashMap<>();
        response.put("policyNumber", policyNumber.trim());
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }
}

