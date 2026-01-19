package com.ycompany.claim.controller;

import com.ycompany.common.dto.ClaimDTO;
import com.ycompany.common.dto.CommentDTO;
import com.ycompany.claim.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
@CrossOrigin(origins = "*")
public class ClaimController {
    
    @Autowired
    private ClaimService claimService;

    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(@Valid @RequestBody ClaimDTO claimDTO) {
        ClaimDTO createdClaim = claimService.createClaim(claimDTO);
        return new ResponseEntity<>(createdClaim, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDTO> getClaimById(@PathVariable Long id) {
        ClaimDTO claim = claimService.getClaimById(id);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ClaimDTO>> getClaimsByCustomerId(@PathVariable String customerId) {
        List<ClaimDTO> claims = claimService.getClaimsByCustomerId(customerId);
        return ResponseEntity.ok(claims);
    }

    @GetMapping
    public ResponseEntity<List<ClaimDTO>> getAllClaims() {
        List<ClaimDTO> claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaimDTO> updateClaim(@PathVariable Long id, @Valid @RequestBody ClaimDTO claimDTO) {
        ClaimDTO updatedClaim = claimService.updateClaim(id, claimDTO);
        return ResponseEntity.ok(updatedClaim);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO) {
        CommentDTO comment = claimService.addComment(id, commentDTO);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long id) {
        List<CommentDTO> comments = claimService.getCommentsByClaimId(id);
        return ResponseEntity.ok(comments);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClaimDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        ClaimDTO updatedClaim = claimService.updateClaimStatus(id, status);
        return ResponseEntity.ok(updatedClaim);
    }
}

