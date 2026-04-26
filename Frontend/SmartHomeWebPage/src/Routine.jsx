import { useEffect, useMemo, useState } from "react";
import './Routine.css'

function Routine({ devices = [], allDevices = [], user, onSelectedDevicesChange }) {
    const commandTypes = ["POWER", "COLOUR", "BRIGHTNESS", "GET_INFO"]
    const daysOfWeek = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"]
    const hourOptions = useMemo(
        () => Array.from({ length: 24 }, (_, index) => String(index).padStart(2, '0')),
        []
    )
    const minuteOptions = useMemo(
        () => Array.from({ length: 60 }, (_, index) => String(index).padStart(2, '0')),
        []
    )
    const refreshPage = () => window.location.reload()

    const [name, setName] = useState('')
    const [selectedDays, setSelectedDays] = useState([])
    const [selectedHour, setSelectedHour] = useState('00')
    const [selectedMinute, setSelectedMinute] = useState('00')
    const [enabled, setEnabled] = useState(true)
    const [routineDevices, setRoutineDevices] = useState([])
    const [routines, setRoutines] = useState([])
    const [showForm, setShowForm] = useState(false)
    const [selectedRoutineId, setSelectedRoutineId] = useState(null)

    const isFormInvalid = routineDevices.length === 0 || selectedDays.length === 0

    const onComponentLoad = async () => {
        if (!user?.id) {
            return
        }

        try {
            const response = await fetch('http://localhost:8080/routine/user', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(user.id)
            })

            if (response.ok) {
                const data = await response.json()
                if (Array.isArray(data)) {
                    setRoutines(data)
                    console.log("Fetched routines:", data)
                } else {
                    console.warn('Expected an array of routines but received:', data)
                    setRoutines([])
                }
            } else {
                console.error('Failed to fetch routines:', response.statusText)
            }
        } catch (error) {
            console.error('Error fetching routines:', error)
        }
    }

    useEffect(() => {
        setRoutineDevices(prev =>
            devices.map(device => {
                const existing = prev.find(item => Number(item.deviceId) === Number(device.id))

                return {
                    deviceId: String(device.id),
                    commandType: existing?.commandType ?? commandTypes[0],
                    commandData: existing?.commandData ?? ''
                }
            })
        )
    }, [devices])

    const resetFormToCreateState = () => {
        setSelectedRoutineId(null)
        setName('')
        setSelectedDays([])
        setSelectedHour('00')
        setSelectedMinute('00')
        setEnabled(true)
        setRoutineDevices(
            devices.map(device => ({
                deviceId: String(device.id),
                commandType: commandTypes[0],
                commandData: ''
            }))
        )
    }

    const openCreateForm = () => {
        resetFormToCreateState()
        setShowForm(true)
    }

    const parseTimeValue = routine => {
        const rawTime = routine.timeToExecute ?? routine.time ?? ''
        if (typeof rawTime !== 'string') {
            return { hour: '00', minute: '00' }
        }

        const [hour = '00', minute = '00'] = rawTime.split(':')
        return {
            hour: String(hour).padStart(2, '0').slice(0, 2),
            minute: String(minute).padStart(2, '0').slice(0, 2)
        }
    }

    const loadRoutineIntoForm = routine => {
        setSelectedRoutineId(routine.id ?? null)
        setName(routine.name ?? '')

        const rawDays = routine.daysToExecute ?? routine.days ?? []
        const normalizedDays = (Array.isArray(rawDays) ? rawDays : [rawDays])
            .filter(Boolean)
            .map(day => String(day).toUpperCase())
        setSelectedDays(normalizedDays)

        const parsedTime = parseTimeValue(routine)
        setSelectedHour(parsedTime.hour)
        setSelectedMinute(parsedTime.minute)

        setEnabled(Boolean(routine.enabled))

        const rawRoutineDevices =
            routine.routineDevices ??
            routine.devices ??
            []

        const normalizedRoutineDevices = (Array.isArray(rawRoutineDevices) ? rawRoutineDevices : [rawRoutineDevices])
            .filter(Boolean)
            .map(item => ({
                deviceId: String(item.deviceId ?? item.deviceID ?? item.id ?? item.device?.id ?? ''),
                commandType: item.commandType ?? item.command ?? commandTypes[0],
                commandData: item.commandData ?? item.data ?? ''
            }))

        setRoutineDevices(normalizedRoutineDevices)

        if (typeof onSelectedDevicesChange === 'function') {
            const selectedDevicesForSidebar = allDevices.filter(device =>
                normalizedRoutineDevices.some(item => Number(item.deviceId) === Number(device.id))
            )
            onSelectedDevicesChange(selectedDevicesForSidebar)
        }

        setShowForm(true)
    }

    const toggleSelectedDay = day => {
        setSelectedDays(prev =>
            prev.includes(day)
                ? prev.filter(selectedDay => selectedDay !== day)
                : [...prev, day]
        )
    }

    const updateRoutineDevice = (deviceId, field, value) => {
        setRoutineDevices(prev =>
            prev.map(item =>
                Number(item.deviceId) === Number(deviceId)
                    ? { ...item, [field]: value }
                    : item
            )
        )
    }

    const buildRoutinePayload = (id = null) => ({
            ...(id !== null ? { id: Number(id) } : {}),
            name: name,
            timeToExecute: `${selectedHour}:${selectedMinute}:00`,
            daysToExecute: selectedDays,
            enabled: enabled,
            userId: Number(user?.id),
            routineDevices: routineDevices.map(item => ({
                deviceId: Number(item.deviceId),
                commandType: item.commandType,
                commandData: item.commandData
            }))
        })

    const handleSubmit = async event => {
        event.preventDefault()

        const payload = buildRoutinePayload()
        console.log('Creating routine payload:', payload)

        try {
            const response = await fetch('http://localhost:8080/routine', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })

            if (response.ok) {
                await onComponentLoad()
                setShowForm(false)
                setSelectedRoutineId(null)
            } else {
                console.error('Failed to create routine:', response.statusText)
            }
        } catch (error) {
            console.error('Error creating routine:', error)
        }
    }

    const handleUpdateRoutine = async () => {
        if (selectedRoutineId === null) {
            console.warn('No routine selected to update.')
            return
        }

        const payload = buildRoutinePayload(selectedRoutineId)
        console.log('Updating routine payload:', payload)

        try {
            const response = await fetch('http://localhost:8080/routine', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })

            if (response.ok) {
                await onComponentLoad()
                setShowForm(false)
                setSelectedRoutineId(null)
                refreshPage()
            } else {
                console.error('Failed to update routine:', response.statusText)
            }
        } catch (error) {
            console.error('Error updating routine:', error)
        }
    }

    const handleDeleteRoutine = async () => {
        if (selectedRoutineId === null) {
            console.warn('No routine selected to delete.')
            return
        }

        try {
            const response = await fetch('http://localhost:8080/routine', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(Number(selectedRoutineId))
            })

            if (response.ok) {
                await onComponentLoad()
                setShowForm(false)
                setSelectedRoutineId(null)
                if (typeof onSelectedDevicesChange === 'function') {
                    onSelectedDevicesChange([])
                }
                refreshPage()
            } else {
                console.error('Failed to delete routine:', response.statusText)
            }
        } catch (error) {
            console.error('Error deleting routine:', error)
        }

    }

    const handleTestRoutine = async () => {
        if (selectedRoutineId === null) {
            console.warn('No routine selected to test.')
            return
        }

        try {
            const response = await fetch('http://localhost:8080/routine/test', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(Number(selectedRoutineId))
            })

            if (!response.ok) {
                console.error('Failed to test routine:', response.statusText)
                return
            }

            console.log('Routine test request sent successfully.')
        } catch (error) {
            console.error('Error testing routine:', error)
        }
    }

    useEffect(() => {
        onComponentLoad()
    }, [user?.id])

    
    return (
        <div className="routine">
            <h1>Routine</h1>
            {!showForm && (
                <div className='routineList'>
                    {routines.length === 0 ? (
                        <div>
                            <p>No routines associated with this user. Press the button to add a routine.</p>
                            <div className='routineActions'>
                                <button type='button' onClick={openCreateForm}>Add Routine</button>
                            </div>
                        </div>
                    ) : (
                        <div>
                            {routines.map(routine => (
                                <button
                                    key={routine.id}
                                    type='button'
                                    className='routineButton'
                                    onClick={() => loadRoutineIntoForm(routine)}
                                >
                                    Routine #{routine.id} - {routine.name}
                                </button>
                            ))}
                            <div className='routineActions'>
                                <button type='button' onClick={openCreateForm}>Add Routine</button>
                            </div>
                        </div>
                    )}
                </div>
            )}

            {showForm && (
            <form className='routineForm' onSubmit={handleSubmit}>
                <label htmlFor='routineName'>Name</label>
                <input
                    id='routineName'
                    type='text'
                    value={name}
                    onChange={event => setName(event.target.value)}
                    placeholder='Morning Lights'
                    required
                />

                <div className='routineSchedule'>
                    <label>Days To Execute</label>
                    <div className='dayCheckboxGrid'>
                        {daysOfWeek.map(day => (
                            <label key={day} className='dayCheckbox'>
                                <input
                                    type='checkbox'
                                    checked={selectedDays.includes(day)}
                                    onChange={() => toggleSelectedDay(day)}
                                />
                                <span>{day}</span>
                            </label>
                        ))}
                    </div>
                </div>

                <div className='timePicker'>
                    <label htmlFor='routineHour'>Hour (24h)</label>
                    <select
                        id='routineHour'
                        value={selectedHour}
                        onChange={event => setSelectedHour(event.target.value)}
                    >
                        {hourOptions.map(hour => (
                            <option key={hour} value={hour}>{hour}</option>
                        ))}
                    </select>

                    <label htmlFor='routineMinute'>Minute</label>
                    <select
                        id='routineMinute'
                        value={selectedMinute}
                        onChange={event => setSelectedMinute(event.target.value)}
                    >
                        {minuteOptions.map(minute => (
                            <option key={minute} value={minute}>{minute}</option>
                        ))}
                    </select>
                </div>

                <label className='enabledCheckbox'>
                    <input
                        type='checkbox'
                        checked={enabled}
                        onChange={event => setEnabled(event.target.checked)}
                    />
                    <span>Is Enabled</span>
                </label>

                <h2>Device Control Parameters</h2>
                {routineDevices.length === 0 ? (
                    <p className='routineHint'>Select one or more devices from the sidebar to configure this routine.</p>
                ) : (
                    <div className='routineDeviceList'>
                        {routineDevices.map(item => {
                            const selectedDevice = allDevices.find(device => Number(device.id) === Number(item.deviceId))

                            return (
                                <div key={item.deviceId} className='routineDeviceCard'>
                                    <p className='routineDeviceTitle'>
                                        {selectedDevice?.name ?? 'Unknown Device'} (ID: {item.deviceId})
                                    </p>

                                    <label htmlFor={`routineCommandType-${item.deviceId}`}>Command Type</label>
                                    <select
                                        id={`routineCommandType-${item.deviceId}`}
                                        value={item.commandType}
                                        onChange={event => updateRoutineDevice(item.deviceId, 'commandType', event.target.value)}
                                    >
                                        {commandTypes.map(commandType => (
                                            <option key={commandType} value={commandType}>{commandType}</option>
                                        ))}
                                    </select>

                                    <label htmlFor={`routineCommandData-${item.deviceId}`}>Command Data</label>
                                    <input
                                        id={`routineCommandData-${item.deviceId}`}
                                        type='text'
                                        value={item.commandData}
                                        onChange={event => updateRoutineDevice(item.deviceId, 'commandData', event.target.value)}
                                        placeholder='e.g. ON, #FFAA00, 60'
                                    />
                                </div>
                            )
                        })}
                    </div>
                )}

                <div className='routineActions'>
                    <button type='submit' disabled={isFormInvalid || selectedRoutineId !== null}>Create Routine</button>
                    {selectedRoutineId !== null && (
                        <button type='button' onClick={handleUpdateRoutine} disabled={isFormInvalid}>Update Routine</button>
                    )}
                    {selectedRoutineId !== null && (
                        <button type='button' onClick={handleTestRoutine}>Test Routine</button>
                    )}
                    {selectedRoutineId !== null && (
                        <button type='button' className='deleteRoutineButton' onClick={handleDeleteRoutine}>Delete Routine</button>
                    )}
                    <button type='button' onClick={() => setShowForm(false)}>Back to Routines</button>
                </div>
            </form>
            )}
       </div>
    )
}

export default Routine