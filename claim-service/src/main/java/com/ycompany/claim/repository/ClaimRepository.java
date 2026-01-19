package com.ycompany.claim.repository;

import com.ycompany.claim.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByCustomerId(String customerId);
    List<Claim> findByStatus(String status);
    List<Claim> findBySupervisorId(String supervisorId);
    boolean existsByPolicyNumber(String policyNumber);
    Optional<Claim> findByPolicyNumber(String policyNumber);
}

