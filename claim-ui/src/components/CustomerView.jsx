import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Grid,
} from '@mui/material'
import { Add, Visibility, Refresh } from '@mui/icons-material'
import { claimService, policyService } from '../services/api'

function CustomerView() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [open, setOpen] = useState(false)
  const [generatingPolicy, setGeneratingPolicy] = useState(false)
  const [formData, setFormData] = useState({
    customerId: 'CUST001',
    policyNumber: '',
    claimType: '',
    description: '',
    claimAmount: '',
    documentUrls: [],
  })

  const { data: allClaims = [], isLoading, refetch } = useQuery(
    'customerClaims',
    () => claimService.getClaimsByCustomer('CUST001').then(res => res.data),
    { refetchInterval: 5000 }
  )

  // Sort claims by creation date (newest first)
  const claims = [...allClaims].sort((a, b) => {
    const dateA = new Date(a.createdAt || 0)
    const dateB = new Date(b.createdAt || 0)
    return dateB - dateA
  })

  const createMutation = useMutation(
    (data) => claimService.createClaim(data),
    {
      onSuccess: async () => {
        // Immediately refetch to show the new claim
        await queryClient.invalidateQueries('customerClaims')
        await refetch()
        handleCloseDialog()
      },
    }
  )

  const handleOpenDialog = async () => {
    setOpen(true)
    // Generate policy number when dialog opens
    await generatePolicyNumber()
  }

  const generatePolicyNumber = async () => {
    setGeneratingPolicy(true)
    try {
      const response = await policyService.generatePolicyNumber('CUST001')
      setFormData(prev => ({
        ...prev,
        policyNumber: response.data.policyNumber
      }))
    } catch (error) {
      console.error('Failed to generate policy number:', error)
      // If generation fails, user can still enter manually
    } finally {
      setGeneratingPolicy(false)
    }
  }

  const handleCloseDialog = () => {
    setOpen(false)
    // Reset form when closing
    setFormData({
      customerId: 'CUST001',
      policyNumber: '',
      claimType: '',
      description: '',
      claimAmount: '',
      documentUrls: [],
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    createMutation.mutate({
      ...formData,
      claimAmount: parseFloat(formData.claimAmount),
    })
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">My Claims</Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleOpenDialog}
        >
          Raise New Claim
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Claim ID</TableCell>
              <TableCell>Policy Number</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Created</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {claims.map((claim) => (
              <TableRow key={claim.id}>
                <TableCell>{claim.id}</TableCell>
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
                <TableCell>
                  {new Date(claim.createdAt).toLocaleDateString()}
                </TableCell>
                <TableCell>
                  <Button
                    size="small"
                    startIcon={<Visibility />}
                    onClick={() => navigate(`/claim/${claim.id}`)}
                  >
                    View
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle>Raise New Claim</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12}>
                <Box sx={{ display: 'flex', gap: 1, alignItems: 'flex-start' }}>
                  <TextField
                    fullWidth
                    label="Policy Number"
                    value={formData.policyNumber}
                    onChange={(e) =>
                      setFormData({ ...formData, policyNumber: e.target.value })
                    }
                    required
                    disabled={generatingPolicy}
                    helperText={generatingPolicy ? 'Generating unique policy number...' : 'Auto-generated unique policy number'}
                    InputProps={{
                      readOnly: true,
                    }}
                  />
                  <Button
                    variant="outlined"
                    startIcon={<Refresh />}
                    onClick={generatePolicyNumber}
                    disabled={generatingPolicy}
                    sx={{ mt: 1 }}
                    size="small"
                  >
                    Regenerate
                  </Button>
                </Box>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Claim Type"
                  value={formData.claimType}
                  onChange={(e) =>
                    setFormData({ ...formData, claimType: e.target.value })
                  }
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  value={formData.description}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  multiline
                  rows={4}
                  required
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Claim Amount"
                  type="number"
                  value={formData.claimAmount}
                  onChange={(e) =>
                    setFormData({ ...formData, claimAmount: e.target.value })
                  }
                  required
                />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button 
              type="submit" 
              variant="contained" 
              disabled={createMutation.isLoading || generatingPolicy || !formData.policyNumber}
            >
              Submit Claim
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  )
}

export default CustomerView

