import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Homepage from './Homepage.jsx'
import Dashboard from './Dashboard.jsx'
import { BrowserRouter, Route, Routes } from 'react-router-dom'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
    <Routes>
      <Route path="/" element={<Homepage />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
    </BrowserRouter>
  </StrictMode>,
)
