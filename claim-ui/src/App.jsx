import React from 'react'
import { Routes, Route, Link, useNavigate } from 'react-router-dom'
import { AppBar, Toolbar, Typography, Button, Container, Box } from '@mui/material'
import CustomerView from './components/CustomerView'
import SupervisorView from './components/SupervisorView'
import ClaimDetails from './components/ClaimDetails'

function App() {
  const navigate = useNavigate()
  const [userRole, setUserRole] = React.useState('customer') // 'customer' or 'supervisor'

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Ycompany Insurance - Claim Management
          </Typography>
          <Button 
            color="inherit" 
            onClick={() => {
              setUserRole(userRole === 'customer' ? 'supervisor' : 'customer')
              navigate('/')
            }}
          >
            Switch to {userRole === 'customer' ? 'Supervisor' : 'Customer'} View
          </Button>
        </Toolbar>
      </AppBar>
      
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Routes>
          <Route 
            path="/" 
            element={userRole === 'customer' ? <CustomerView /> : <SupervisorView />} 
          />
          <Route path="/claim/:id" element={<ClaimDetails />} />
        </Routes>
      </Container>
    </Box>
  )
}

export default App

