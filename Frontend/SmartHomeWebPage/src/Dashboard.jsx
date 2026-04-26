import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Dashboard.css'
import Controller from './Controller.jsx'
import Device from './device.jsx'
import Trigger from './Trigger.jsx'
import Routine from './Routine.jsx'
import AccountSettings from './AccountSettings.jsx'

function Dashboard() {
  const [jsonString, setJsonString] = useState("")
  const [user, setUser] = useState(null)
  const [userDevices, setUserDevices] = useState([])
  const [activeDevice, setActiveDevice] = useState(null)
  const [activeRoutineDevices, setActiveRoutineDevices] = useState([])
  const [showAddMenu, setShowAddMenu] = useState(false)
  const [activeMenu, setActiveMenu] = useState(null)
  const navigate = useNavigate()

  const routineSidebarDevices = userDevices.filter(device => device.type === 'LIFX' || device.type === 'MICROCONTROLLER')
  
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
    console.log(user.username)
    try {
      const response = await fetch('http://localhost:8080/link', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({"id": user.id})
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

  function toggleMenu(menu) {
    setActiveMenu((prev) => {
      const nextMenu = prev === menu ? null : menu

      if (prev === 'trigger' && nextMenu === null) {
        setActiveDevice(null)
      }

      if (nextMenu === 'routine') {
        setActiveRoutineDevices([])
      }

      if (prev === 'routine' && nextMenu !== 'routine') {
        setActiveRoutineDevices([])
      }

      return nextMenu
    })
  }

  function renderDashboardContent(jsxObject = null) {
    if (jsxObject) return jsxObject

    if (activeMenu === 'device') {
      return <Device addNewDevice={false} user={user} linkedDevices={userDevices} />
    }

    if (activeMenu === 'addDevice') {
      return <Device addNewDevice={true} user={user} />
    }

    if (activeMenu === 'trigger') {
      return <Trigger userDevices={userDevices} device={activeDevice} />
    }

    if (activeMenu === 'routine') {
      return (
        <Routine
          devices={activeRoutineDevices}
          allDevices={routineSidebarDevices}
          user={user}
          onSelectedDevicesChange={setActiveRoutineDevices}
        />
      )
    }

    if (activeMenu === 'account') {
      return <AccountSettings username={user.username} />
    }

    return <Controller device={activeDevice} user={user} />
  }

  document.title = `Dashboard - ${user.username}`

  return (
    <div className='bg'>
      <div className='navbar'>
        <h1>Dashboard</h1>
        <h1>Welcome, {user.username}. Please pick or add a device</h1>
        <button onClick={() => {toggleMenu('account')}}>{activeMenu !== 'account' ? "Account Settings" : "Return"}</button>
        <button onClick={logout}>Logout</button>
      </div>
      <div className='deviceBar'>
        <h2>
          {activeMenu === 'trigger'
            ? 'Source Devices'
            : activeMenu === 'routine'
              ? 'Routine Devices'
              : 'My Devices'}
        </h2>
        {userDevices
          .filter((device) => {
            if (activeMenu === 'trigger') {
              return device.type === 'SENSOR'
            }

            return routineSidebarDevices.some(routineDevice => routineDevice.id === device.id)
          })
          .map((device, index) => (
          <button
            key={index}
            className={`deviceButton${
              activeMenu === 'routine'
                ? activeRoutineDevices.some(selectedDevice => selectedDevice.id === device.id)
                  ? ' active'
                  : ''
                : activeDevice?.id === device.id
                  ? ' active'
                  : ''
            }`}
            onClick={() => {
              if (activeMenu === 'trigger') {
                setActiveDevice(device)
              } else if (activeMenu === 'routine') {
                setActiveRoutineDevices(prev => {
                  const alreadySelected = prev.some(selectedDevice => selectedDevice.id === device.id)

                  if (alreadySelected) {
                    return prev.filter(selectedDevice => selectedDevice.id !== device.id)
                  }

                  return [...prev, device]
                })
              } else {
                setActiveDevice(device)
                setActiveMenu(null)
              }
              console.log("Active device:", device);
            }}
          >
            (ID: {device.id}) {device.name}
          </button>
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
            <button onClick={() => toggleMenu('device')}>{activeMenu !== 'device' ? "Add Existing Device" : "Return to Control Menu"}</button>
            <button onClick={() => toggleMenu('addDevice')}>{activeMenu !== 'addDevice' ? "Add New Device" : "Return to Control Menu"}</button>
            <button onClick={() => toggleMenu('trigger')}>{activeMenu !== 'trigger' ? "Add Trigger" : "Return to Control Menu"}</button>
            <button onClick={() => toggleMenu('routine')}>{activeMenu !== 'routine' ? "Add Routine" : "Return to Control Menu"}</button>
          </div>
        </div>
        {renderDashboardContent()}
      </div>
    </div>
  )
}

export default Dashboard