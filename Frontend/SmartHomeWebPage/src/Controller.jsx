import React from 'react';

function Controller({ device }) {
  if (!device) {
    return (
      <div className="controller">
        <h1>Controller</h1>
        <h2>No device selected</h2>
      </div>
    );
  }

  // Example function to run on button click
  const handleButtonClick = async () => {
    const payload = {
      userId: 1,
      deviceId: device.id,
      deviceType: device.type,
      commandType: "POWER",
      commandData: ""
    };
    try {
      const response = await fetch("http://localhost:8080/device/control", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Timeout": "2000" // Set a timeout of 5 seconds
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
  };

  return (
    <div className="controller">
      <h1>Controller</h1>
      <h2>Active Device</h2>
      <p>{device && device.name}</p>
      <div className="controller-center">
        <button className="central-button" onClick={handleButtonClick}>
          Toggle Power
        </button>
      </div>
    </div>
  );
}

export default Controller;
