# Smart Home Hub
**Final Year Project – Tom Ellingworth**

## Overview
A locally hosted smart home hub designed to integrate devices from multiple ecosystems into a single, unified platform. The system prioritises privacy, reliability, and extensibility by removing dependence on cloud services and enabling full local control.

The project consists of a Java Spring Boot backend, a React frontend, and custom ESP8266-based devices and sensors.

---

## Features
- User authentication with secure password hashing
- Device discovery using mDNS (no manual IP configuration required)
- Device control (custom ESP8266 devices and LIFX integration)
- Device-user linking system
- Automation through:
    - Triggers (sensor-based)
    - Routines (time-based scheduling)
- Local-only operation for improved privacy and reliability

---

## Architecture
- **Backend:** Java Spring Boot REST API
- **Frontend:** React single-page application
- **Devices:** ESP8266 microcontrollers (custom firmware)
- **Communication:** HTTP + mDNS (local network only)

---

## Setup

### Backend
1. Navigate to: `src/main/java/com.project.smarthomehub`
2. Run `FinalYearProjectApplication.java` or via terminal: `./gradlew bootRun`, which will start the backend server on `http://localhost:8080`. 

---

### Frontend
1. Navigate to the `Frontend/SmartHomeWebPage` directory
2. Run: `npm install` to install dependencies
3. Run: `npm run dev` to start the development server, which will open the frontend in your default browser at `http://localhost:5173`.
---

### ESP8266 Setup

1. Flash the provided firmware onto the ESP8266
2. Connect to the device's Wi-Fi network
3. Navigate to: `192.168.4.1`
4. Enter your Wi-Fi credentials and configure the device, device will automatically reboot and connect to the local network.

To add the device:
- Open the frontend
- Click the "+" button
- Select **Add New Device**
- Select **Microcontroller**
- Run device discovery
- Select **Add Existing Device**
- Press **Get All Devices** and select your device from the list - should no device appear, ensure the device is connected to the same network and try discovery again.
If this continues to not work, the ESP will print its IP Address to Serial, this combined with the manuel adding can be used.
- Click **Add** to link it to your account
- Once added, you can control the device and link it to automations.
- Controllable devices appear in the **Sidebar**, Sensor devices appears in the **Triggers** page.
---

## Sensor Usage

Ensure the **sensor firmware** is flashed (`-Sensor` version).

All sensors connect to the **D1 pin**.

### Supported Sensors

#### PIR Motion Sensor (HC-SR501)
- VCC → 3.3V
- GND → GND
- OUT → D1

#### Magnetic Door Sensor
- One wire → D1
- One wire → 3.3V
- Add 10kΩ pull-down resistor between D1 wire and GND

---

## LIFX Integration
The system supports LIFX smart devices via their HTTP API.

### Setup
1. Obtain an API key from the LIFX developer portal
2. In the frontend:
- Add a new device
- Select **LIFX**
- Enter API key

> Note: Testing may be limited due to reduced availability of LIFX devices in the UK.

---

## Requirements
- Java 17+
- Node.js and npm
- ESP8266 modules
- Local network environment

### Optional
- LIFX devices and API key

---

## Limitations
- No mobile-optimised frontend (not completed within timeframe)
- Limited third-party integrations (LIFX only)
- Requires local network setup

---

## Motivation
This project addresses the fragmentation of smart home ecosystems by providing a unified, extensible platform that operates entirely on a local network. This improves privacy, reduces reliance on external services, and enables greater flexibility in device integration.

---

## Future Improvements
- Mobile-compatible frontend
- Additional third-party integrations
- Improved UI/UX
- Expanded automation capabilities

---

## Original Repository

The code for this project was developed in the following GitHub repository: https://github.com/OnlyJustTom/SmartHomeHub

Here all commit messages and development history can be viewed.

---

## Author
Tom Ellingworth  
University of Leicester – Computer Science Final Year Project