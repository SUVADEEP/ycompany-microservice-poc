package com.ycompany.claim.service;

import com.ycompany.claim.entity.Claim;
import com.ycompany.claim.entity.Comment;
import com.ycompany.claim.repository.ClaimRepository;
import com.ycompany.claim.repository.CommentRepository;
import com.ycompany.common.dto.ClaimDTO;
import com.ycompany.common.dto.CommentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private TemporalWorkflowService temporalWorkflowService;
    
    @Autowired
    private PolicyService policyService;

    @Transactional
    public ClaimDTO createClaim(ClaimDTO claimDTO) {
        Claim claim = new Claim();
        claim.setCustomerId(claimDTO.getCustomerId());
        
        // Auto-generate policy number if not provided
        String policyNumber = claimDTO.getPolicyNumber();
        if (policyNumber == null || policyNumber.trim().isEmpty()) {
            policyNumber = policyService.generateUniquePolicyNumber(claimDTO.getCustomerId());
            logger.info("Auto-generated policy number: {} for customer: {}", policyNumber, claimDTO.getCustomerId());
        }
        claim.setPolicyNumber(policyNumber);
        
        claim.setClaimType(claimDTO.getClaimType());
        claim.setDescription(claimDTO.getDescription());
        claim.setClaimAmount(claimDTO.getClaimAmount());
        claim.setStatus("PENDING");
        if (claimDTO.getDocumentUrls() != null) {
            claim.setDocumentUrls(claimDTO.getDocumentUrls());
        }
        
        Claim savedClaim = claimRepository.save(claim);
        
        // Initiate Temporal workflow - wrap in try-catch to prevent transaction rollback
        // if Temporal is unavailable, the claim should still be saved
        try {
            temporalWorkflowService.startClaimWorkflow(savedClaim.getId());
            logger.info("Successfully started Temporal workflow for claim {}", savedClaim.getId());
        } catch (Exception e) {
            // Log the error but don't fail the claim creation
            // The claim is already saved, so we just log the Temporal workflow failure
            logger.error("Failed to start Temporal workflow for claim {}: {}", savedClaim.getId(), e.getMessage(), e);
        }
        
        return convertToDTO(savedClaim);
    }

    public ClaimDTO getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        return convertToDTO(claim);
    }

    public List<ClaimDTO> getClaimsByCustomerId(String customerId) {
        return claimRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimDTO updateClaim(Long id, ClaimDTO claimDTO) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        
        claim.setClaimType(claimDTO.getClaimType());
        claim.setDescription(claimDTO.getDescription());
        claim.setClaimAmount(claimDTO.getClaimAmount());
        if (claimDTO.getDocumentUrls() != null) {
            claim.setDocumentUrls(claimDTO.getDocumentUrls());
        }
        
        Claim updatedClaim = claimRepository.save(claim);
        return convertToDTO(updatedClaim);
    }

    @Transactional
    public CommentDTO addComment(Long claimId, CommentDTO commentDTO) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));
        
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setAuthorId(commentDTO.getAuthorId());
        comment.setAuthorName(commentDTO.getAuthorName());
        comment.setClaim(claim);
        
        Comment savedComment = commentRepository.save(comment);
        return convertToCommentDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByClaimId(Long claimId) {
        return commentRepository.findByClaimId(claimId)
                .stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimDTO updateClaimStatus(Long id, String status) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
        claim.setStatus(status);
        Claim updatedClaim = claimRepository.save(claim);
        return convertToDTO(updatedClaim);
    }

    private ClaimDTO convertToDTO(Claim claim) {
        ClaimDTO dto = new ClaimDTO();
        dto.setId(claim.getId());
        dto.setCustomerId(claim.getCustomerId());
        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimType(claim.getClaimType());
        dto.setDescription(claim.getDescription());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setStatus(claim.getStatus());
        dto.setCreatedAt(claim.getCreatedAt());
        dto.setUpdatedAt(claim.getUpdatedAt());
        dto.setDocumentUrls(claim.getDocumentUrls());
        dto.setSupervisorId(claim.getSupervisorId());
        dto.setComments(claim.getComments().stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorId(comment.getAuthorId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}

