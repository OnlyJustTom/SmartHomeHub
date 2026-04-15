import { useEffect, useMemo, useState } from 'react'
import './Trigger.css'

function Trigger({ userDevices }) {
  const triggerConditions = ['MOTION_DETECTED', 'DOOR_OPENED', 'SENSOR_TOUCHED']
  const commandTypes = ['POWER', 'BRIGHTNESS', 'COLOR', 'GET_INFO']

  const [sensorDevice, setSensorDevice] = useState([])
  const [triggerDevice, setTriggerDevice] = useState(0)
  const [triggerCondition, setTriggerCondition] = useState(triggerConditions[0])
  const [targetDevices, setTargetDevices] = useState([
    { deviceId: 0, commandType: commandTypes[0], commandData: '' }
  ])
  const [generatedPayload, setGeneratedPayload] = useState(null)

  useEffect(() => {
    const sensors = userDevices.filter(device => device.type === 'SENSOR')
    setSensorDevice(sensors)

    // Set a sensible default trigger device when sensor data first loads.
    if (sensors.length > 0 && !triggerDevice) {
      setTriggerDevice(String(sensors[0].id))
    }
  }, [userDevices, triggerDevice])

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

  const handleSubmit = event => {
    event.preventDefault()

    // Build the payload so it matches the Java class fields exactly.
    const payload = {
      triggerDevice: triggerDevice,
      triggerCondition: triggerCondition,
      targetDevices: targetDevices.map(device => ({
        deviceId: device.deviceId,
        commandType: device.commandType,
        commandData: device.commandData
      }))
    }

    setGeneratedPayload(payload)
    console.log('Trigger payload:', payload)
  }

  return (
    <div className='trigger'>
      <h1>Trigger</h1>

      <form className='triggerForm' onSubmit={handleSubmit}>
        <label htmlFor='triggerDevice'>Trigger Device (Integer)</label>
        <select
          id='triggerDevice'
          value={triggerDevice}
          onChange={event => setTriggerDevice(event.target.value)}
          required
        >
          {sensorDevice.length === 0 ? (
            <option value=''>No sensor devices found</option>
          ) : (
            sensorDevice.map(device => (
              <option key={device.id} value={device.id}>
                {device.name} (ID: {device.id})
              </option>
            ))
          )}
        </select>

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
          <button type='submit'>Build payload</button>
        </div>
      </form>
    </div>
  )
}

export default Trigger