import React, { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from 'react-query'
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  TextField,
  Chip,
  Grid,
  List,
  ListItem,
  ListItemText,
  Divider,
  Paper,
} from '@mui/material'
import { ArrowBack, Send } from '@mui/icons-material'
import { claimService } from '../services/api'

function ClaimDetails() {
  const { id } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [comment, setComment] = useState('')
  const [userRole] = useState('customer') // In real app, get from auth context

  const { data: claim, isLoading } = useQuery(
    ['claim', id],
    () => claimService.getClaim(id).then(res => res.data),
    { refetchInterval: 5000 }
  )

  const commentMutation = useMutation(
    (data) => claimService.addComment(id, data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['claim', id])
        setComment('')
      },
    }
  )

  const handleAddComment = () => {
    if (comment.trim()) {
      commentMutation.mutate({
        text: comment,
        authorId: userRole === 'customer' ? 'CUST001' : 'SUPER001',
        authorName: userRole === 'customer' ? 'Customer' : 'Supervisor',
      })
    }
  }

  if (isLoading) return <Typography>Loading...</Typography>
  if (!claim) return <Typography>Claim not found</Typography>

  return (
    <Box>
      <Button
        startIcon={<ArrowBack />}
        onClick={() => navigate('/')}
        sx={{ mb: 2 }}
      >
        Back
      </Button>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Claim #{claim.id}
          </Typography>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} md={6}>
              <Typography variant="body2" color="text.secondary">
                Customer ID
              </Typography>
              <Typography variant="body1">{claim.customerId}</Typography>
            </Grid>
            <Grid item xs={12} md={6}>
              <Typography variant="body2" color="text.secondary">
                Policy Number
              </Typography>
              <Typography variant="body1">{claim.policyNumber}</Typography>
            </Grid>
            <Grid item xs={12} md={6}>
              <Typography variant="body2" color="text.secondary">
                Claim Type
              </Typography>
              <Typography variant="body1">{claim.claimType}</Typography>
            </Grid>
            <Grid item xs={12} md={6}>
              <Typography variant="body2" color="text.secondary">
                Claim Amount
              </Typography>
              <Typography variant="body1">${claim.claimAmount?.toFixed(2)}</Typography>
            </Grid>
            <Grid item xs={12} md={6}>
              <Typography variant="body2" color="text.secondary">
                Status
              </Typography>
              <Chip
                label={claim.status}
                color={
                  claim.status === 'APPROVED'
                    ? 'success'
                    : claim.status === 'REJECTED'
                    ? 'error'
                    : 'warning'
                }
              />
            </Grid>
            <Grid item xs={12}>
              <Typography variant="body2" color="text.secondary">
                Description
              </Typography>
              <Typography variant="body1">{claim.description}</Typography>
            </Grid>
            {claim.documentUrls && claim.documentUrls.length > 0 && (
              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">
                  Documents
                </Typography>
                {claim.documentUrls.map((url, idx) => (
                  <Typography key={idx} variant="body2">
                    <a href={url} target="_blank" rel="noopener noreferrer">
                      Document {idx + 1}
                    </a>
                  </Typography>
                ))}
              </Grid>
            )}
          </Grid>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Comments
          </Typography>
          <List>
            {claim.comments && claim.comments.length > 0 ? (
              claim.comments.map((comment, idx) => (
                <React.Fragment key={comment.id}>
                  <ListItem>
                    <ListItemText
                      primary={comment.authorName}
                      secondary={
                        <>
                          <Typography component="span" variant="body2">
                            {comment.text}
                          </Typography>
                          <Typography
                            component="span"
                            variant="caption"
                            display="block"
                            color="text.secondary"
                          >
                            {new Date(comment.createdAt).toLocaleString()}
                          </Typography>
                        </>
                      }
                    />
                  </ListItem>
                  {idx < claim.comments.length - 1 && <Divider />}
                </React.Fragment>
              ))
            ) : (
              <Typography variant="body2" color="text.secondary">
                No comments yet
              </Typography>
            )}
          </List>
          <Box sx={{ mt: 2 }}>
            <TextField
              fullWidth
              multiline
              rows={3}
              placeholder="Add a comment..."
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              sx={{ mb: 1 }}
            />
            <Button
              variant="contained"
              startIcon={<Send />}
              onClick={handleAddComment}
              disabled={!comment.trim() || commentMutation.isLoading}
            >
              Add Comment
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  )
}

export default ClaimDetails

