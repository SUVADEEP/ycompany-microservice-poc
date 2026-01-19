import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  MenuItem,
} from '@mui/material'
import { Visibility, Check, Close } from '@mui/icons-material'
import { workflowService, claimService } from '../services/api'

function SupervisorView() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [approvalDialog, setApprovalDialog] = useState({ open: false, claim: null })
  const [approvalData, setApprovalData] = useState({
    decision: 'APPROVED',
    comments: '',
  })

  const { data: claims = [], isLoading } = useQuery(
    'allClaims',
    () => claimService.getAllClaims().then(res => res.data),
    { refetchInterval: 5000 }
  )

  const approvalMutation = useMutation(
    (data) => workflowService.approveClaim(data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('allClaims')
        setApprovalDialog({ open: false, claim: null })
        setApprovalData({ decision: 'APPROVED', comments: '' })
      },
    }
  )

  const handleApprove = () => {
    if (approvalDialog.claim) {
      approvalMutation.mutate({
        claimId: approvalDialog.claim.id,
        supervisorId: 'SUPER001',
        decision: approvalData.decision,
        comments: approvalData.comments,
      })
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return 'success'
      case 'REJECTED': return 'error'
      case 'PENDING': return 'warning'
      default: return 'default'
    }
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 3 }}>
        Supervisor Dashboard - All Claims
      </Typography>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Claim ID</TableCell>
              <TableCell>Customer ID</TableCell>
              <TableCell>Policy Number</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Supervisor</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {claims.map((claim) => (
              <TableRow key={claim.id}>
                <TableCell>{claim.id}</TableCell>
                <TableCell>{claim.customerId}</TableCell>
                <TableCell>{claim.policyNumber}</TableCell>
                <TableCell>{claim.claimType}</TableCell>
                <TableCell>${claim.claimAmount?.toFixed(2)}</TableCell>
                <TableCell>
                  <Chip
                    label={claim.status}
                    color={getStatusColor(claim.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>{claim.supervisorId || 'Unassigned'}</TableCell>
                <TableCell>
                  <Button
                    size="small"
                    startIcon={<Visibility />}
                    onClick={() => navigate(`/claim/${claim.id}`)}
                    sx={{ mr: 1 }}
                  >
                    View
                  </Button>
                  {claim.status === 'PENDING' && (
                    <Button
                      size="small"
                      variant="contained"
                      color="primary"
                      startIcon={<Check />}
                      onClick={() =>
                        setApprovalDialog({ open: true, claim })
                      }
                    >
                      Review
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog
        open={approvalDialog.open}
        onClose={() => setApprovalDialog({ open: false, claim: null })}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Review Claim #{approvalDialog.claim?.id}
        </DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            select
            label="Decision"
            value={approvalData.decision}
            onChange={(e) =>
              setApprovalData({ ...approvalData, decision: e.target.value })
            }
            sx={{ mt: 2, mb: 2 }}
          >
            <MenuItem value="APPROVED">Approve</MenuItem>
            <MenuItem value="REJECTED">Reject</MenuItem>
          </TextField>
          <TextField
            fullWidth
            label="Comments"
            value={approvalData.comments}
            onChange={(e) =>
              setApprovalData({ ...approvalData, comments: e.target.value })
            }
            multiline
            rows={4}
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setApprovalDialog({ open: false, claim: null })}
          >
            Cancel
          </Button>
          <Button
            variant="contained"
            onClick={handleApprove}
            disabled={approvalMutation.isLoading}
          >
            Submit Decision
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default SupervisorView

