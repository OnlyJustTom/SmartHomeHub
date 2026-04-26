import React, { useEffect, useState } from 'react';
import './Controller.css'

function Controller({ device, user }) {
  const [lifxCommandType, setLifxCommandType] = useState('COLOUR')
  const [redValue, setRedValue] = useState('255')
  const [greenValue, setGreenValue] = useState('255')
  const [blueValue, setBlueValue] = useState('255')
  const [brightnessValue, setBrightnessValue] = useState('1')
  const [showEditForm, setShowEditForm] = useState(false)
  const [editName, setEditName] = useState('')
  const [editAPIKeyIP, setEditAPIKeyIP] = useState('')
  const [editType, setEditType] = useState('LIFX')
  const refreshPage = () => window.location.reload()

  useEffect(() => {
    if (!device) {
      return
    }

    setShowEditForm(false)
    setEditName(device.name ?? '')
    setEditAPIKeyIP(device.APIKeyIP ?? device.apiKeyIP ?? '')
    setEditType(device.type ?? 'LIFX')
    setLifxCommandType('COLOUR')
    setRedValue('255')
    setGreenValue('255')
    setBlueValue('255')
    setBrightnessValue('1')
  }, [device?.id])

  if (!device) {
    return (
      <div className="controller">
        <h1>Controller</h1>
        <div className='controllerPanel'>
          <h2>No device selected</h2>
        </div>
      </div>
    );
  }

  const submitControlCommand = async payload => {
    try {
      const response = await fetch("http://localhost:8080/device/control", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        console.log("Command sent successfully! (HTTP 200 OK)");
      } else if (response.status === 400) {
        console.log("Bad request (HTTP 400 Bad Request)");
      } else {
        console.log(`Unexpected error: HTTP ${response.status}`);
      }
    } catch (error) {
      console.log("Failed to send command: " + error);
    }
  }

  const handleMicrocontrollerPower = async () => {
    const payload = {
      userId: Number(user?.id),
      deviceId: Number(device.id),
      deviceType: device.type,
      commandType: "POWER",
      commandData: ""
    };

    await submitControlCommand(payload)
  }

  const handleLifxSubmit = async event => {
    event.preventDefault()

    let commandData = ''

    switch (lifxCommandType) {
      case 'COLOUR':
        commandData = `rgb:${redValue},${greenValue},${blueValue}`
        break
      case 'BRIGHTNESS':
        commandData = brightnessValue
        break
      default:
        commandData = ''
        break
    }

    const payload = {
      userId: Number(user?.id),
      deviceId: Number(device.id),
      deviceType: device.type,
      commandType: lifxCommandType,
      commandData: commandData
    }

    await submitControlCommand(payload)
  }

  const handleResetMicrocontroller = async () => {
    try {
      const response = await fetch("http://localhost:8080/device/microcontroller", {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify(Number(device.id))
      })

      if (response.ok) {
        console.log('Microcontroller reset request sent successfully!')
        refreshPage()
      } else {
        console.log(`Failed to reset microcontroller: HTTP ${response.status}`)
      }
    } catch (error) {
      console.log('Failed to reset microcontroller: ' + error)
    }
  }

  const handleEditDevice = async event => {
    event.preventDefault()

    const payload = {
      id: Number(device.id),
      APIKeyIP: editAPIKeyIP,
      type: editType
    }

    try {
      const response = await fetch('http://localhost:8080/device', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (response.ok) {
        console.log('Device updated successfully!')
        setShowEditForm(false)
      } else {
        console.log(`Failed to update device: HTTP ${response.status}`)
      }
    } catch (error) {
      console.log('Failed to update device: ' + error)
    }
  }

  const handleUnlinkDevice = async () => {

    console.log(JSON.stringify({"userId": user.id, "deviceId": device.id}))
    try {
      const response = await fetch('http://localhost:8080/link', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({"userID": user.id, "deviceID": device.id})
      })

      if (response.ok) {
        console.log('Device unlinked successfully!')
        refreshPage()
      } else {
        console.log(`Failed to unlink device: HTTP ${response.status}`)
      }
    } catch (error) {
      console.log('Failed to unlink device: ' + error)
    }
  }

  const handleDeleteDevice = async () => {
    try {
      
      const response = await fetch('http://localhost:8080/device', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({"id": device.id, "APIKeyIP": device.APIKeyIP, "name": device.name, "type": device.type})
      })

      if (response.ok) {
        console.log('Device deleted successfully!')
        refreshPage()
      } else {
        console.log(`Failed to delete device: HTTP ${response.status}`)
      }
    } catch (error) {
      console.log('Failed to delete device: ' + error)
    }
  }

  const renderEditForm = () => {
    if (!showEditForm) {
      return null
    }

    return (
      <form className='controllerForm controllerEditForm' onSubmit={handleEditDevice}>
        <h3>Edit Device</h3>

        <label htmlFor='editDeviceName'>Name</label>
        <input
          id='editDeviceName'
          type='text'
          value={editName}
          onChange={event => setEditName(event.target.value)}
          placeholder='Device Name'
        />

        <label htmlFor='editAPIKeyIP'>API Key / IP Address</label>
        <input
          id='editAPIKeyIP'
          type='text'
          value={editAPIKeyIP}
          onChange={event => setEditAPIKeyIP(event.target.value)}
          placeholder='API key or IP address'
          required
        />

        <label htmlFor='editDeviceType'>Type</label>
        <select
          id='editDeviceType'
          value={editType}
          onChange={event => setEditType(event.target.value)}
        >
          <option value='LIFX'>LIFX</option>
          <option value='MICROCONTROLLER'>MICROCONTROLLER</option>
          <option value='SENSOR'>SENSOR</option>
        </select>

        <div className='controllerActions'>
          <button type='submit'>Save Device</button>
        </div>
      </form>
    )
  }

  switch (device.type) {
    case "LIFX":
      return (
        <div className="controller">
          <h1>Controller</h1>
          <div className='controllerPanel'>
            <h2>Controlling LIFX Device: {device.name}</h2>

            <form className='controllerForm controllerCommandForm' onSubmit={handleLifxSubmit}>
              <label htmlFor='lifxCommandType'>Command Type</label>
              <select
                id='lifxCommandType'
                value={lifxCommandType}
                onChange={event => setLifxCommandType(event.target.value)}
              >
                <option value='COLOUR'>COLOUR</option>
                <option value='BRIGHTNESS'>BRIGHTNESS</option>
                <option value='POWER'>POWER</option>
              </select>

              <label htmlFor='lifxRed'>R</label>
              <input
                id='lifxRed'
                type='text'
                value={redValue}
                onChange={event => setRedValue(event.target.value)}
                placeholder='0-255'
              />

              <label htmlFor='lifxGreen'>G</label>
              <input
                id='lifxGreen'
                type='text'
                value={greenValue}
                onChange={event => setGreenValue(event.target.value)}
                placeholder='0-255'
              />

              <label htmlFor='lifxBlue'>B</label>
              <input
                id='lifxBlue'
                type='text'
                value={blueValue}
                onChange={event => setBlueValue(event.target.value)}
                placeholder='0-255'
              />

              <label htmlFor='lifxBrightness'>Brightness</label>
              <input
                id='lifxBrightness'
                type='text'
                value={brightnessValue}
                onChange={event => setBrightnessValue(event.target.value)}
                placeholder='0 to 1'
              />

              <div className='controllerActions'>
                <button type='submit'>Submit Command</button>
              </div>
            </form>

            <div className='controllerActions'>
              <button type='button' onClick={() => setShowEditForm(prev => !prev)}>Edit Device</button>
              <button type='button' className='controllerDangerButton' onClick={handleUnlinkDevice}>Unlink Device</button>
              <button type='button' className='controllerDangerButton' onClick={handleDeleteDevice}>Delete Device</button>
            </div>
            {renderEditForm()}
          </div>
        </div>
      );

    case "MICROCONTROLLER":
      return (
    <div className="controller">
      <h1>Controller</h1>
      <div className='controllerPanel'>
        <h2>Active Device</h2>
        <p className='controllerDeviceName'>{device && device.name}</p>
        <div className="controller-center">
          <button className="central-button" onClick={handleMicrocontrollerPower}>
            Toggle Power
          </button>
        </div>

        <div className='controllerActions'>
          <button type='button' className='controllerDangerButton' onClick={handleResetMicrocontroller}>Reset Microcontroller</button>
          <button type='button' onClick={() => setShowEditForm(prev => !prev)}>Edit Device</button>
          <button type='button' className='controllerDangerButton' onClick={handleUnlinkDevice}>Unlink Device</button>
          <button type='button' className='controllerDangerButton' onClick={handleDeleteDevice}>Delete Device</button>
        </div>
        {renderEditForm()}
      </div>
    </div>
  );

    default:
      return (
        <div className="controller">
          <h1>Controller</h1>
          <div className='controllerPanel'>
            <h2>Unsupported Device Type: {device.type}</h2>
            <div className='controllerActions'>
              <button type='button' onClick={() => setShowEditForm(prev => !prev)}>Edit Device</button>
              <button type='button' className='controllerDangerButton' onClick={handleDeleteDevice}>Delete Device</button>
            </div>
            {renderEditForm()}
          </div>
        </div>
      );
}
}

export default Controller;
