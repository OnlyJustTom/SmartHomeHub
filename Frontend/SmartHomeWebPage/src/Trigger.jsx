import { useEffect, useMemo, useState } from 'react'
import './Trigger.css'

function Trigger({ userDevices, device }) {
  const triggerConditions = ['MOTION_DETECTED', 'DOOR_OPENED', 'SENSOR_TOUCHED']
  const commandTypes = ['POWER', 'BRIGHTNESS', 'COLOUR', 'GET_INFO']

  const [triggerCondition, setTriggerCondition] = useState(triggerConditions[0])
  const [targetDevices, setTargetDevices] = useState([
    { deviceId: '', commandType: commandTypes[0], commandData: '' }
  ])
  const [generatedPayload, setGeneratedPayload] = useState(null)
  const [triggers, setTriggers] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [selectedTriggerId, setSelectedTriggerId] = useState(null)
  const isMicrocontrollerSelected = device?.type === 'SENSOR'
  const hasSourceDeviceSelected = Boolean(device?.id)

  useEffect(() => {
    if (device?.id) {
      setSelectedTriggerId(null)
      setShowForm(false)
      setTriggerCondition(triggerConditions[0])
      setTargetDevices([{ deviceId: '', commandType: commandTypes[0], commandData: '' }])
    }
  }, [device?.id])

  // Target devices are usually controllable devices, so sensors are excluded.
  const availableTargetDevices = useMemo(
    () => userDevices.filter(device => device.type !== 'SENSOR'),
    [userDevices]
  )

  const addTargetDevice = () => {
    setTargetDevices(prev => [...prev, { deviceId: '', commandType: commandTypes[0], commandData: '' }])
  }

  const removeTargetDevice = indexToRemove => {
    setTargetDevices(prev => prev.filter((_, index) => index !== indexToRemove))
  }

  const updateTargetDevice = (indexToUpdate, field, value) => {
    setTargetDevices(prev =>
      prev.map((item, index) => (index === indexToUpdate ? { ...item, [field]: value } : item))
    )
  }

  const openCreateForm = () => {
    setSelectedTriggerId(null)
    setTriggerCondition(triggerConditions[0])
    setTargetDevices([{ deviceId: '', commandType: commandTypes[0], commandData: '' }])
    setShowForm(true)
  }

  const loadTriggerIntoForm = trigger => {
    setSelectedTriggerId(trigger.id ?? null)
    setTriggerCondition(trigger.triggerCondition)

    console.log("Trigger targets" + trigger.targetDevices )

    const rawTargets =
      trigger.targetDevices ??
      trigger.triggerDevices ??
      trigger.devices ??
      []

    const normalizedTargets = (Array.isArray(rawTargets) ? rawTargets : [rawTargets])
      .filter(Boolean)
      .map(target => ({
        deviceId: String(target.deviceId ?? target.deviceID ?? target.id ?? target.device?.id ?? ''),
        commandType: target.commandType ?? target.command ?? commandTypes[0],
        commandData: target.commandData ?? target.data ?? ''
      }))

    if (normalizedTargets.length > 0) {
      setTargetDevices(normalizedTargets)
    } else {
      setTargetDevices([{ deviceId: '', commandType: commandTypes[0], commandData: '' }])
    }

    setShowForm(true)
  }

  const syncSelectedTriggerDevice = async selectedDeviceId => {
    if (!selectedDeviceId) {
      setTriggers([])
      return
    }

    const payload = Number(selectedDeviceId)
    setGeneratedPayload(payload)
    console.log('Trigger payload:', payload)

    try {
      const response = await fetch('http://localhost:8080/triggers/device', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        console.error('Failed to submit trigger device ID:', response.statusText)
      }
      else {
        console.log('Trigger device ID submitted successfully!')

        // Deserialize API JSON into a list of trigger objects.
        const data = await response.json()
        if (Array.isArray(data)) {
          setTriggers(data)
          console.log('Deserialized trigger objects:', data)
        } else {
          console.warn('Expected an array of triggers but received:', data)
          setTriggers([])
        }
      }
    } catch (error) {
      console.error('Error submitting trigger device ID:', error)
    }
  }

  useEffect(() => {
    syncSelectedTriggerDevice(device?.id)
  }, [device?.id])

  const handleSubmit = async event => {
    event.preventDefault()

    if (selectedTriggerId !== null) {
      console.warn('Submit is disabled for existing triggers.')
      return
    }

    const basePayload = {
      triggerDevice: Number(device?.id),
      triggerCondition: triggerCondition,
      targetDevices: targetDevices.map(target => ({
        deviceId: Number(target.deviceId),
        commandType: target.commandType,
        commandData: target.commandData
      }))
    }

    const isEditingExistingTrigger = selectedTriggerId !== null
    const payload = isEditingExistingTrigger
      ? { id: Number(selectedTriggerId), ...basePayload }
      : basePayload

    setGeneratedPayload(payload)
    try {
      const response = await fetch('http://localhost:8080/triggers', {
        method: isEditingExistingTrigger ? 'PATCH' : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        console.error('Failed to save trigger:', response.statusText)
      } else {
        console.log(isEditingExistingTrigger ? 'Trigger updated successfully!' : 'Trigger saved successfully!')
        syncSelectedTriggerDevice(device?.id)
        setSelectedTriggerId(null)
        setShowForm(false)
      }
    } catch (error) {
      console.error('Error saving trigger:', error)
    }

    console.log('Submitting trigger form payload:', payload)
  }

  const handleDeleteTrigger = async () => {
    if (selectedTriggerId === null) {
      console.warn('No trigger selected to delete.')
      return
    }

    const payload = Number(selectedTriggerId)

    try {
      const response = await fetch('http://localhost:8080/triggers', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        console.error('Failed to delete trigger:', response.statusText)
      } else {
        console.log('Trigger deleted successfully!')
        setSelectedTriggerId(null)
        setShowForm(false)
        setTriggerCondition(triggerConditions[0])
        setTargetDevices([{ deviceId: '', commandType: commandTypes[0], commandData: '' }])
        syncSelectedTriggerDevice(device?.id)
      }
    } catch (error) {
      console.error('Error deleting trigger:', error)
    }
  }

  const handleTestTrigger = async () => {
    if (selectedTriggerId === null) {
      console.warn('No trigger selected to test.')
      return
    }

    const payload = Number(selectedTriggerId)

    try {
      const response = await fetch('http://localhost:8080/triggers/test', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        console.error('Failed to test trigger:', response.statusText)
      } else {
        console.log('Trigger test request sent successfully!')
      }
    } catch (error) {
      console.error('Error testing trigger:', error)
    }
  }

  const handleResetMicrocontroller = async () => {
    if (!device?.id || !isMicrocontrollerSelected) {
      console.warn('Please select a microcontroller device to reset.')
      return
    }

    console.log(JSON.stringify(device.id))

    try {
      const response = await fetch('http://localhost:8080/device/microcontroller', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: device.id
      })

      if (!response.ok) {
        console.error('Failed to reset microcontroller:', response.statusText)
      } else {
        console.log('Microcontroller reset request sent successfully!')
        window.location.reload()
      }
    } catch (error) {
      console.error('Error resetting microcontroller:', error)
    }
  }

  return (
    <div className='trigger'>
      <p className='triggerHint'>Click on a source device to view or create triggers.</p>
      {!showForm && (
        <div className='triggerList'>
          {triggers.length === 0 ? (
            <div>
              <p>No Triggers associated with this Device. Press the button to add a Trigger</p>
              <div className='triggerActions'>
                <button type='button' onClick={openCreateForm} disabled={!hasSourceDeviceSelected}>Add Trigger</button>
                <button type='button' onClick={handleResetMicrocontroller} disabled={!isMicrocontrollerSelected}>Reset Microcontroller</button>
              </div>
            </div>
          ) : (
            <div>
                {triggers.map(trigger => (
                <button
                  key={trigger.id}
                  type='button'
                  className='triggerButton'
                  onClick={() => loadTriggerIntoForm(trigger)}
                >
                  Trigger #{trigger.id} - {trigger.triggerCondition}
                </button>
              ))}
              <div className='triggerActions'>
                <button type='button' onClick={openCreateForm} disabled={!hasSourceDeviceSelected}>Add Trigger</button>
                                <button type='button' onClick={handleResetMicrocontroller} disabled={!isMicrocontrollerSelected}>Reset Microcontroller</button>
              </div>
     
            </div>
          )}
        </div>
      )}

      {showForm && (
        <form className='triggerForm' onSubmit={handleSubmit}>
          <label htmlFor='triggerCondition'>Trigger Condition (Enum)</label>
          <select
            id='triggerCondition'
            value={triggerCondition}
            onChange={event => setTriggerCondition(event.target.value)}
            required
          >
            {triggerConditions.map(condition => (
              <option key={condition} value={condition}>
                {condition}
              </option>
            ))}
          </select>

          <h2>Target Devices (List&lt;TriggerDeviceRequst&gt;)</h2>

          <div className='targetDeviceList'>
            {targetDevices.map((target, index) => (
              <div className='targetDeviceCard' key={`target-${index}`}>
                <label htmlFor={`targetDevice-${index}`}>Device ID</label>
                <select
                  id={`targetDevice-${index}`}
                  value={target.deviceId}
                  onChange={event => updateTargetDevice(index, 'deviceId', event.target.value)}
                  required
                >
                  <option value=''>Select target device</option>
                  {availableTargetDevices.map(device => (
                    <option key={device.id} value={device.id}>
                      {device.name} (ID: {device.id})
                    </option>
                  ))}
                </select>

                <label htmlFor={`commandType-${index}`}>Command Type</label>
                <select
                  id={`commandType-${index}`}
                  value={target.commandType}
                  onChange={event => updateTargetDevice(index, 'commandType', event.target.value)}
                  required
                >
                  {commandTypes.map(commandType => (
                    <option key={commandType} value={commandType}>
                      {commandType}
                    </option>
                  ))}
                </select>

                <label htmlFor={`commandData-${index}`}>Command Data (String)</label>
                <input
                  id={`commandData-${index}`}
                  type='text'
                  value={target.commandData}
                  onChange={event => updateTargetDevice(index, 'commandData', event.target.value)}
                  placeholder='e.g. ON, 60, #FFAA00'
                />

                <button
                  type='button'
                  className='removeTargetButton'
                  onClick={() => removeTargetDevice(index)}
                  disabled={targetDevices.length === 1}
                >
                  Remove target
                </button>
              </div>
            ))}
          </div>

          <div className='triggerActions'>
            <button type='button' onClick={addTargetDevice}>Add target device</button>
            <button type='submit' disabled={selectedTriggerId !== null}>Submit</button>
            {selectedTriggerId !== null && (
              <>
                <button type='button' onClick={handleTestTrigger}>Test Trigger</button>
                <button type='button' onClick={handleDeleteTrigger}>Delete Trigger</button>
              </>
            )}
            <button type='button' onClick={() => setShowForm(false)}>Back to Triggers</button>
          </div>
        </form>
      )}
    </div>
  )
}

export default Trigger