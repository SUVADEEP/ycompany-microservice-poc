import React from 'react'
import { Routes, Route, useNavigate, Navigate } from 'react-router-dom'
import { AppBar, Toolbar, Typography, Button, Container, Box, Chip } from '@mui/material'
import { Logout, Person } from '@mui/icons-material'
import { useAuth } from './context/AuthContext'
import CustomerView from './components/CustomerView'
import SupervisorView from './components/SupervisorView'
import ClaimDetails from './components/ClaimDetails'
import Login from './components/Login'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  const navigate = useNavigate()
  const { user, logout, isCustomer, isApprover, isAuthenticated } = useAuth()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      {user && (
        <AppBar position="static">
          <Toolbar>
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              Ycompany Insurance - Claim Management
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Chip
                icon={<Person />}
                label={`${user.username} (${user.role === 'customer' ? 'Customer' : 'Approver'})`}
                color="secondary"
                variant="outlined"
                sx={{ color: 'white', borderColor: 'rgba(255,255,255,0.5)' }}
              />
              <Button
                color="inherit"
                startIcon={<Logout />}
                onClick={handleLogout}
              >
                Logout
              </Button>
            </Box>
          </Toolbar>
        </AppBar>
      )}
      
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Routes>
          <Route 
            path="/login" 
            element={isAuthenticated ? <Navigate to="/" replace /> : <Login />} 
          />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                {isCustomer ? <CustomerView /> : <SupervisorView />}
              </ProtectedRoute>
            }
          />
          <Route
            path="/claim/:id"
            element={
              <ProtectedRoute>
                <ClaimDetails />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Container>
    </Box>
  )
}

export default App

