package com.ycompany.claim.service;

import com.ycompany.claim.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class PolicyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);
    
    @Autowired
    private ClaimRepository claimRepository;
    
    private final Random random = new Random();
    private static final String POLICY_PREFIX = "POL";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * Generates a unique policy number for a customer
     * Format: POL-{customerId}-{date}-{random4digits}
     * Example: POL-CUST001-20260119-1234
     * 
     * @param customerId The customer ID
     * @return A unique policy number
     */
    public String generateUniquePolicyNumber(String customerId) {
        String policyNumber;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            // Format: POL-{customerId}-{YYYYMMDD}-{4digitRandom}
            String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
            int randomNum = 1000 + random.nextInt(9000); // 4-digit random number (1000-9999)
            policyNumber = String.format("%s-%s-%s-%d", POLICY_PREFIX, customerId, dateStr, randomNum);
            
            attempts++;
            
            // Check if policy number already exists
            if (!claimRepository.existsByPolicyNumber(policyNumber)) {
                logger.info("Generated unique policy number: {} for customer: {}", policyNumber, customerId);
                return policyNumber;
            }
            
            logger.warn("Policy number {} already exists, generating new one (attempt {})", policyNumber, attempts);
            
        } while (attempts < maxAttempts);
        
        // Fallback: add timestamp to ensure uniqueness
        long timestamp = System.currentTimeMillis();
        policyNumber = String.format("%s-%s-%s-%d-%d", POLICY_PREFIX, customerId, 
                LocalDateTime.now().format(DATE_FORMATTER), random.nextInt(10000), timestamp % 10000);
        
        logger.warn("Using fallback policy number generation: {} for customer: {}", policyNumber, customerId);
        return policyNumber;
    }
    
    /**
     * Validates if a policy number is already in use
     * 
     * @param policyNumber The policy number to check
     * @return true if policy number exists, false otherwise
     */
    public boolean isPolicyNumberExists(String policyNumber) {
        return claimRepository.existsByPolicyNumber(policyNumber);
    }
}

