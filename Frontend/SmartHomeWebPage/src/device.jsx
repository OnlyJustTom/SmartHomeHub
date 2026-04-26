import React, { useMemo, useState } from 'react';
import './device.css'

function Device({ addNewDevice, user, linkedDevices = [] }) {
    const [devices, setDevices] = useState([]);
    const [selected, setSelected] = useState([]);
    const deviceTypes = ["LIFX", "MICROCONTROLLER"];
    const [selectedDeviceType, setSelectedDeviceType] = useState(deviceTypes[0]);
    const [apiKey, setApiKey] = useState('');
    const refreshPage = () => window.location.reload()

    const linkedDeviceIds = useMemo(
        () => new Set(linkedDevices.map(device => Number(device.id))),
        [linkedDevices]
    )

    const filteredDevices = useMemo(
        () => devices.filter(device => !linkedDeviceIds.has(Number(device.id))),
        [devices, linkedDeviceIds]
    )

    const getDevices = async (e) => {
        try {
            const response = await fetch('http://localhost:8080/device/all', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
            let data = null
            if(response.ok){
                data = await response.json()
                const filteredData = Array.isArray(data)
                    ? data.filter(device => !linkedDeviceIds.has(Number(device.id)))
                    : []
                setDevices(filteredData)
                console.log(data)
            }
            else{
                setDevices([])
            }
        }
        catch (error) {
            console.error('Error sending user info:', error)
        }
    }

    const toggleSelect = (device) => {
        setSelected((prev) => {
            if (prev.some((d) => d.id === device.id)) {
                return prev.filter((d) => d.id !== device.id);
            } else {
                return [...prev, device];
            }
        });
    };

    const handleAdd = async () => {
        const successfullyLinkedDeviceIds = []

        for (const device of selected) {
            try {
                const response = await fetch('http://localhost:8080/link', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        userID: user.id,
                        deviceID: device.id
                    })
                });
                if (response.ok) {
                    console.log(`Linked device ${device.id} successfully.`);
                    successfullyLinkedDeviceIds.push(Number(device.id))
                } else {
                    console.error(`Failed to link device ${device.id}:`, response.statusText);
                }
            } catch (error) {
                console.error(`Error linking device ${device.id}:`, error);
            }
        }

        if (successfullyLinkedDeviceIds.length > 0) {
            refreshPage()
        }
    };

    const handleNewDeviceSubmit = async () => {
        if (selectedDeviceType === 'LIFX' && !apiKey.trim()) {
            console.error('API key is required for LIFX devices.')
            return
        }

        const payload = {
            userId: user.id,
            deviceId: 0,
            deviceType: selectedDeviceType,
            commandType: "GET_INFO",
            commandData: selectedDeviceType === 'LIFX' ? apiKey.trim() : ""
        }

        try {
            const response = await fetch('http://localhost:8080/device/control', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })

            if (response.ok) {
                console.log('GET_INFO command sent successfully!', payload)
                refreshPage()
            } else {
                console.error('Failed to send GET_INFO command:', response.statusText)
            }
        } catch (error) {
            console.error('Error sending GET_INFO command:', error)
        }
    };


    if (addNewDevice == false) {
        return (
            <div className="device">
                <h1>Device</h1>
                <button onClick={getDevices}>Get All Devices</button>
                <div className="device-list">
                    {filteredDevices.map((device) => {
                        const isSelected = selected.some((d) => d.id === device.id);
                        return (
                            <button
                                key={device.id}
                                className={"device-btn" + (isSelected ? " selected" : "")}
                                onClick={() => toggleSelect(device)}
                            >
                                {isSelected ? '✔ ' : ''}
                                <strong>{device.name}</strong> ({device.type})
                            </button>
                        );
                    })}
                </div>
                <button onClick={handleAdd} disabled={selected.length === 0} style={{marginTop: '1em'}}>
                    Add
                </button>
            </div>
        );
    }
    else{
        return (
            <div className="device device-add">
                <h1>Add new Device</h1>
                <div className="deviceForm">
                <div className="deviceActions">
                    <button className="deviceCircleButton" type="button" onClick={handleNewDeviceSubmit}>
                        +
                    </button>
                </div>

                <div className="deviceFieldGroup">
                    <label htmlFor="deviceType">Device Type</label>
                    <select
                        id="deviceType"
                        value={selectedDeviceType}
                        onChange={(e) => setSelectedDeviceType(e.target.value)}
                    >
                        {deviceTypes.map((type) => (
                            <option key={type} value={type}>
                                {type}
                            </option>
                        ))}
                    </select>
                </div>

                {selectedDeviceType === 'LIFX' && (
                    <div className="deviceFieldGroup">
                        <label htmlFor="apiKey">API Key</label>
                        <input
                            id="apiKey"
                            type="text"
                            value={apiKey}
                            onChange={(e) => setApiKey(e.target.value)}
                            placeholder="Enter LIFX API key"
                            required
                        />
                    </div>
                )}
                </div>
            </div>
            
        );
    }
}

export default Device;