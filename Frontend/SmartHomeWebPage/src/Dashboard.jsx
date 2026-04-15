import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Dashboard.css'
import Controller from './Controller.jsx'
import Device from './device.jsx'
import Trigger from './Trigger.jsx'
import Routine from './Routine.jsx'

function Dashboard() {
  const [jsonString, setJsonString] = useState("")
  const [user, setUser] = useState(null)
  const [userDevices, setUserDevices] = useState([])
  const [activeDevice, setActiveDevice] = useState(null)
  const [showAddMenu, setShowAddMenu] = useState(false)
  const [activeMenu, setActiveMenu] = useState(null)
  const [showDeviceMenu, setShowDeviceMenu] = useState(false)
  const [showAddDeviceMenu, setShowAddDeviceMenu] = useState(false)
  const [showTriggerMenu, setShowTriggerMenu] = useState(false)
  const [showRoutineMenu, setShowRoutineMenu] = useState(false)
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

  useEffect(() => {
    if (jsonString) {
      getUserDevices();
    }
  }, [jsonString]);

  if (!user) {
    return <p>Loading...</p>
  }

 const getUserDevices = async (e) => {
    try {
      const response = await fetch('http://localhost:8080/link', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: jsonString
      })
      
      let data = null

      if(response.ok){
        data = await response.json()
        setUserDevices(data)
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
    setShowAddMenu((prev) => !prev);
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
        <h2>My Devices</h2>
        {userDevices.map((device, index) => (
          <button key={index} onClick={() => {
            setActiveDevice(device);
            setShowAddDeviceMenu(false);
            setShowDeviceMenu(false);
            console.log("Active device:", device);
          }}>(ID: {device.id}) {device.name}</button>
        ))}
      </div>
      <div className='deviceControl'>
        <div className='addDeviceContainer'>
          <button className={"addDevice" + (showAddMenu ? " open" : "")} title='Add Device' onClick={handleAddDevice}>+</button>
          <div
            className={"addDeviceMenuPopup" + (showAddMenu ? " open" : "")}
            style={{ display: showAddMenu || true ? 'flex' : 'none' }}
            onClick={e => e.stopPropagation()}
          >
            <button onClick={() => { setShowDeviceMenu(!showDeviceMenu); setActiveMenu('device');}}>{!showDeviceMenu ? "Add Existing Device" : "Return to Control Menu"}</button>
            <button onClick={() => { setShowAddDeviceMenu(!showAddDeviceMenu); setActiveMenu('addDevice');}}>{!showAddDeviceMenu ? "Add New Device" : "Return to Control Menu"}</button>
            <button onClick={() => { setShowTriggerMenu(!showTriggerMenu); setActiveMenu('trigger')}}>{!showTriggerMenu ? "Add Trigger" : "Return to Control Menu"}</button>
            <button onClick={() => { setShowRoutineMenu(!showRoutineMenu); setActiveMenu('routine')}}>{!showRoutineMenu ? "Add Routine" : "Return to Control Menu"}</button>
          </div>
        </div>
        {activeMenu === 'device' && showDeviceMenu ? <Device addNewDevice={showAddDeviceMenu} user={user}/> : activeMenu === 'addDevice' && showAddDeviceMenu ? <Device addNewDevice={showAddDeviceMenu} user={user}/> : activeMenu === 'trigger' && showTriggerMenu ? <Trigger userDevices={userDevices}/> : activeMenu === 'routine' && showRoutineMenu ? <Routine/> : <Controller device={activeDevice}/> }
      </div>
    </div>
  )
}

export default Dashboard