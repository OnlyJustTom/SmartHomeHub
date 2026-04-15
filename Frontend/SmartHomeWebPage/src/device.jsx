import React, { useState } from 'react';
import './device.css'

function Device({ addNewDevice, user }) {
    const [name, setName] = useState('');
    const [apiKeyIPAddress, setApiKeyIpAddress] = useState('');
    const [devices, setDevices] = useState([]);
    const [selected, setSelected] = useState([]);
    const [deviceTypes, setDeviceTypes] = useState(["Lifx", "Microcontroller", "Sensor"]);
    const [selectedDeviceType, setSelectedDeviceType] = useState("Lifx");

    const getDevices = async (e) => {
        try {
            const response = await fetch('http://localhost:8080/device/all', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
            let data = null
            if(response.ok){
                data = await response.json()
                setDevices(data)
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
                } else {
                    console.error(`Failed to link device ${device.id}:`, response.statusText);
                }
            } catch (error) {
                console.error(`Error linking device ${device.id}:`, error);
            }
        }
    };

    const handleNewDeviceSubmit = async (e) => {
        e.preventDefault();
        
        console.log(JSON.stringify({
            name: name,
            APIKeyIP: apiKeyIPAddress,
            type: selectedDeviceType.toUpperCase()
        }))

        console.log("Submitting new device with details:", { name, apiKeyIPAddress, selectedDeviceType });
        
        try{
            const response = await fetch('http://localhost:8080/device', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    name: name,
                    APIKeyIP: apiKeyIPAddress,
                    type: selectedDeviceType.toUpperCase()
                })
            })

            

            if(response.ok){
                const data = await response.json();
                console.log(data);
            }
            else{
                console.error("Failed to add device:", response.statusText)
            }
        }
        catch(error){
            console.error("Error adding device:", error)
        }
        
    };


    if (addNewDevice == false) {
        return (
            <div className="device">
                <h1>Device</h1>
                <button onClick={getDevices}>Get All Devices</button>
                <div className="device-list">
                    {devices.map((device) => {
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
            <div className="device">
                <h1>Add new Device</h1>
                <form onSubmit={handleNewDeviceSubmit}>
                    <div>
                        <label htmlFor="name">Name</label>
                        <input
                            id="name"
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="apiKey">APIKey / IPAddress</label>
                        <input
                            id="apiKey"
                            type="text"
                            value={apiKeyIPAddress}
                            onChange={(e) => setApiKeyIpAddress(e.target.value)}
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="deviceType">Device Type</label>
                        <select
                            id="deviceType"
                            value={selectedDeviceType}
                            onChange={(e) => setSelectedDeviceType(e.target.value)}
                            required
                        >
                            {deviceTypes.map((type) => (
                                <option key={type} value={type}>
                                    {type}
                                </option>
                            ))}
                        </select>
                    </div>

                    <button type="submit" style={{ marginTop: '1em' }}>Submit</button>
                </form>
            </div>
            
        );
    }
}

export default Device;