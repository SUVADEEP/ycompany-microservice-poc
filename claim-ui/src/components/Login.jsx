import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Tabs,
  Tab,
  Alert
} from '@mui/material'
import { useAuth } from '../context/AuthContext'

function Login() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [tabValue, setTabValue] = useState(0)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue)
    setUsername('')
    setPassword('')
    setError('')
  }

  const handleLogin = (e) => {
    e.preventDefault()
    setError('')

    // Simple validation - in production, this would call an authentication API
    if (!username.trim()) {
      setError('Please enter a username')
      return
    }

    if (!password.trim()) {
      setError('Please enter a password')
      return
    }

    // Determine role based on selected tab
    const role = tabValue === 0 ? 'customer' : 'approver'

    // Simple authentication - in production, validate against backend
    // For demo purposes, accept any username/password
    login(username, role)
    navigate('/')
  }

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f5f5f5'
      }}
    >
      <Card sx={{ maxWidth: 400, width: '100%', m: 2 }}>
        <CardContent>
          <Typography variant="h4" component="h1" gutterBottom align="center" sx={{ mb: 3 }}>
            Ycompany Insurance
          </Typography>
          <Typography variant="h6" component="h2" gutterBottom align="center" sx={{ mb: 3 }}>
            Claim Management System
          </Typography>

          <Tabs value={tabValue} onChange={handleTabChange} variant="fullWidth" sx={{ mb: 3 }}>
            <Tab label="Customer Login" />
            <Tab label="Approver Login" />
          </Tabs>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleLogin}>
            <TextField
              fullWidth
              label="Username"
              variant="outlined"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              margin="normal"
              required
              autoFocus
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              margin="normal"
              required
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              sx={{ mt: 3, mb: 2 }}
              size="large"
            >
              {tabValue === 0 ? 'Login as Customer' : 'Login as Approver'}
            </Button>
          </Box>

          <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 2 }}>
            Demo: Enter any username and password to login
          </Typography>
        </CardContent>
      </Card>
    </Box>
  )
}

export default Login

