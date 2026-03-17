import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Dashboard.css'
import Controller from './Controller.jsx'

function Dashboard() {
  const [jsonString, setJsonString] = useState("")
  const [user, setUser] = useState(null)
  const [devices, setDevices] = useState([])
  const [activeDevice, setActiveDevice] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    const savedUser = localStorage.getItem("user")

    if (!savedUser) {
      navigate("/")
    }

    try {
      setJsonString(savedUser)
      const parsedUser = JSON.parse(savedUser)
      setUser(parsedUser)
    } catch (error) {
      console.error("Failed to parse user from localStorage:", error)
      localStorage.removeItem("user")
      navigate("/")
    }
  }, [navigate])

  const logout = () => {
    localStorage.removeItem("user")
    navigate("/")
  }

  if (!user) {
    return <p>Loading...</p>
  }

  const getUserDevices = async (e) => {
    e.preventDefault()

    try {
      const response = await fetch('http://localhost:8080/link', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: jsonString
      })
      
      let data = null

      if(response.ok){
        data = await response.json()
        setDevices(data)
        console.log("User devices:", data)
      }
      else{
        console.error('Failed to send user info:', response.statusText)
      }
      
    } catch (error) {
      console.error('Error sending user info:', error)
    }
  }


  function handleAddDevice() {
    // Logic to add a new device
    console.log("Add Device button clicked")
  }

  document.title = `Dashboard - ${user.username}`

  return (
    <div className='bg'>
      <div className='navbar'>
        <h1>Dashboard</h1>
        <h1>Welcome, {user.username}. Please pick or add a device</h1>
        <button onClick={logout}>Logout</button>
      </div>
      <div className='deviceBar'>
        <h2>Devices</h2>
        {devices.map((device, index) => (
          <button key={index} onClick={() => {
            setActiveDevice(device);
            console.log("Active device:", device);
          }}>{device.name} (ID: {device.id})</button>
        ))}
      </div>
      <div className='deviceControl'>
        <button className='addDevice' title='Add Device' onClick={getUserDevices}>+</button>
        <Controller jsonString={JSON.stringify(activeDevice)}/>
      </div>
    </div>
  )
}

export default Dashboard


// <div style={{ padding: "2rem" }}>
    //   <h1>Dashboard</h1>
    //   <p>Welcome, {user.username}</p>

    //   <p>User ID: {user.id}</p>

    //   <button onClick={logout}>Logout</button>
    // </div>