#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <ESP8266HTTPClient.h>
#include <DNSServer.h>
#include "ConfigStore.h"

const byte DNS_PORT = 53;
DNSServer dnsServer;
ESP8266WebServer server(80);

#ifndef LED_BUILTIN
#define LED_BUILTIN 2
#endif

const char* AP_SSID = "ESP8266-Sensor-Setup";
const char* AP_PASSWORD = "";

const char* DEFAULT_TRIGGER_CONDITION = "MOTION_DETECTED";
const char* TRIGGER_CONDITION_OPTIONS[] = {
  "MOTION_DETECTED",
  "DOOR_OPENED",
  "SENSOR_TOUCHED"
};


enum TriggerConditions {
  MOTION_DETECTED,
  DOOR_OPENED,
  SENSOR_TOUCHED
};


WifiConfig config;
bool ledOn = false;


IPAddress SERVER_IP;
const uint16_t SERVER_PORT = 8080;
const char* TARGET_PATH = "/triggers";
const uint8_t SENSOR_PIN = D1;


bool SensorEventSent = false;

bool isValidTriggerCondition(const String& value) {
  for (const char* option : TRIGGER_CONDITION_OPTIONS) {
    if (value.equals(option)) {
      return true;
    }
  }
  return false;
}

String getConfiguredTriggerCondition() {
  String condition = String(config.triggerCondition);
  condition.trim();

  if (!isValidTriggerCondition(condition)) {
    condition = DEFAULT_TRIGGER_CONDITION;
  }

  return condition;
}

bool loadServerIpFromConfig() {
  String serverIp = String(config.serverIp);
  serverIp.trim();

  if (serverIp.length() == 0) {
    SERVER_IP = IPAddress();
    return false;
  }

  return SERVER_IP.fromString(serverIp);
}

// -------------------- Helpers --------------------

String jsonEscape(const String& s) {
  String out;
  out.reserve(s.length() + 10);
  for (size_t i = 0; i < s.length(); i++) {
    char c = s[i];
    if (c == '\"') out += "\\\"";
    else if (c == '\\') out += "\\\\";
    else if (c == '\n') out += "\\n";
    else if (c == '\r') out += "\\r";
    else if (c == '\t') out += "\\t";
    else out += c;
  }
  return out;
}

void setLed(bool on) {
  ledOn = on;
  digitalWrite(LED_BUILTIN, on ? LOW : HIGH);
}

// -------------------- Normal mode handlers --------------------

void handleRoot() {
  server.send(200, "text/plain", "ok");
}

void handleStatus() {
  Serial.println("Status requested");

  String body = "{";
  body += "\"name\":\"" + String(config.hostname) + "\",";
  body += "\"ip\":\"" + WiFi.localIP().toString() + "\",";
  body += "\"deviceType\":\"SENSOR\"";
  body += "}";

  SERVER_IP = server.client().remoteIP();
  String serverIp = SERVER_IP.toString();
  serverIp.toCharArray(config.serverIp, sizeof(config.serverIp));

  saveConfig(
    String(config.ssid),
    String(config.password),
    String(config.hostname),
    getConfiguredTriggerCondition(),
    serverIp
  );

  Serial.println("Server IP set to" + SERVER_IP.toString());

  server.send(200, "application/json", body);
}

void sendSensorEvent(const IPAddress& targetIp, uint16_t port, const char* path) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("Skipping sensor event: WiFi not connected");
    return;
  }
  Serial.println("Sending sensor event to " + targetIp.toString() + ":" + String(port) + path);

  WiFiClient client;
  HTTPClient http;
  String url = "http://" + targetIp.toString() + ":" + String(port) + String(path);

  if (!http.begin(client, url)) {
    Serial.println("Failed to initialize HTTP client for motion event");
    return;
  }

  http.addHeader("Content-Type", "application/json");
  
  const String payload = "{\"deviceIPAddress\":\"" + WiFi.localIP().toString() + "\",\"triggerCondition\":\"" + getConfiguredTriggerCondition() + "\"}";
  Serial.println("Payload: " + payload);
  int statusCode = http.POST(payload);

  Serial.print("Sensor event POST status: ");
  Serial.println(statusCode);

  http.end();
}

void handleSensorTest() {
  if (server.method() != HTTP_POST) {
    server.send(405, "application/json", "{\"error\":\"Use POST\"}");
    return;
  }


  SensorEventSent = false;
  server.send(200, "application/json", "{\"ok\":true,\"sensorFlag\":1}");
}

void processTrigger() {
  if (digitalRead(SENSOR_PIN) == HIGH) {
    if (!SensorEventSent && SERVER_IP) {
      sendSensorEvent(SERVER_IP, SERVER_PORT, TARGET_PATH);
      SensorEventSent = true;
    }
    return;
  }

  SensorEventSent = false;
}

void handleToggleLed() {
  if (server.method() != HTTP_POST) {
    server.send(405, "application/json", "{\"error\":\"Use POST\"}");
    return;
  }

  setLed(!ledOn);
  server.send(
    200,
    "application/json",
    String("{\"ok\":true,\"led\":") + (ledOn ? "true" : "false") + "}"
  );
}

void handleReset() {
  server.send(200, "text/html",
    "<html><body style='font-family:Arial;text-align:center;margin-top:50px;'>"
    "<h2>Clearing saved config...</h2>"
    "<p>Rebooting into setup mode.</p>"
    "</body></html>"
  );

  delay(1500);
  MDNS.end();
  clearConfig();
  ESP.restart();
}

void handleNotFound() {
  String body = "{\"error\":\"not_found\",\"path\":\"" + jsonEscape(server.uri()) + "\"}";
  server.send(404, "application/json", body);
}

// -------------------- Setup portal handlers --------------------

void handleSetupPage() {
  String html = R"rawliteral(
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>ESP8266 Setup</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          margin: 30px;
          background: #f4f4f4;
        }
        .container {
          max-width: 400px;
          margin: auto;
          background: white;
          padding: 20px;
          border-radius: 10px;
          box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 {
          text-align: center;
        }
        input[type="text"], input[type="password"] {
          width: 100%;
          padding: 10px;
          margin: 8px 0 16px 0;
          border: 1px solid #ccc;
          border-radius: 5px;
          box-sizing: border-box;
        }
        input[type="submit"] {
          width: 100%;
          background: #007bff;
          color: white;
          padding: 12px;
          border: none;
          border-radius: 5px;
          cursor: pointer;
          font-size: 16px;
        }
        input[type="submit"]:hover {
          background: #0056b3;
        }
        label {
          font-weight: bold;
        }
      </style>
    </head>
    <body>
      <div class="container">
        <h2>ESP8266 WiFi Setup</h2>
        <form action="/submit" method="POST">
          <label for="ssid">WiFi Name (SSID)</label>
          <input type="text" id="ssid" name="ssid" required>

          <label for="password">WiFi Password</label>
          <input type="password" id="password" name="password">

          <label for="hostname">Hostname</label>
          <input type="text" id="hostname" name="hostname" required>

          <label for="triggerCondition">Trigger Condition</label>
          <select id="triggerCondition" name="triggerCondition">
            <option value="MOTION_DETECTED" selected>Motion Detected</option>
            <option value="DOOR_OPENED">Door Opened</option>
            <option value="SENSOR_TOUCHED">Sensor Touched</option>
          </select>

          <input type="submit" value="Save and Reboot">
        </form>
      </div>
    </body>
    </html>
  )rawliteral";

  server.send(200, "text/html", html);
}

void handleSubmit() {
  String ssid = server.arg("ssid");
  String password = server.arg("password");
  String hostname = server.arg("hostname");
  String triggerCondition = server.arg("triggerCondition");

  ssid.trim();
  password.trim();
  hostname.trim();
  triggerCondition.trim();

  if (ssid.length() == 0 || hostname.length() == 0) {
    server.send(400, "text/html", "<h2>SSID and Hostname are required</h2>");
    return;
  }

  if (!isValidTriggerCondition(triggerCondition)) {
    triggerCondition = DEFAULT_TRIGGER_CONDITION;
  }

  if (!saveConfig(ssid, password, hostname, triggerCondition, String(config.serverIp))) {
    server.send(500, "text/html", "<h2>Failed to save config</h2>");
    return;
  }

  server.send(200, "text/html",
    "<html><body style='font-family:Arial;text-align:center;margin-top:50px;'>"
    "<h2>Saved successfully</h2>"
    "<p>Rebooting into normal mode...</p>"
    "</body></html>"
  );

  delay(1500);
  ESP.restart();
}

// -------------------- Mode startup --------------------

void startSetupMode() {
  Serial.println("Starting setup mode...");

  WiFi.disconnect();
  WiFi.mode(WIFI_AP);
  WiFi.softAP(AP_SSID, AP_PASSWORD);

  IPAddress apIP = WiFi.softAPIP();
  dnsServer.start(DNS_PORT, "*", apIP);

  Serial.print("AP SSID: ");
  Serial.println(AP_SSID);
  Serial.print("AP IP: ");
  Serial.println(WiFi.softAPIP());

  server.on("/generate_204", HTTP_GET, handleSetupPage);     // Android
  server.on("/hotspot-detect.html", HTTP_GET, handleSetupPage); // Apple
  server.on("/ncsi.txt", HTTP_GET, handleSetupPage);         // Windows

  server.on("/", HTTP_GET, handleSetupPage);
  server.on("/submit", HTTP_POST, handleSubmit);
  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("Setup web server started");
}

void startNormalMode() {
  Serial.println("Starting normal mode...");

  WiFi.mode(WIFI_STA);

  String host = String(config.hostname);
  host.trim();

  if (host.length() > 0) {
    WiFi.hostname(host);
  }

  WiFi.begin(config.ssid, config.password);

  Serial.print("Connecting to WiFi");
  unsigned long startAttempt = millis();

  while (WiFi.status() != WL_CONNECTED && millis() - startAttempt < 20000) {
    delay(300);
    Serial.print(".");
  }
  Serial.println();

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi failed, switching to setup mode");
    startSetupMode();
    return;
  }

  if (!MDNS.begin(config.hostname)) {
    Serial.println("Error starting mDNS");
  } else {
    MDNS.addService("smarthub", "tcp", 80);
    Serial.print("mDNS started: http://");
    Serial.print(config.hostname);
    Serial.println(".local");
  }

  Serial.print("Connected! IP: ");
  Serial.println(WiFi.localIP());

  server.on("/", HTTP_GET, handleRoot);
  server.on("/api/status", HTTP_GET, handleStatus);
  server.on("/api/test", HTTP_POST, handleSensorTest);
  server.on("/reset", HTTP_POST, handleReset);
  server.on("/api/toggle", HTTP_POST, handleToggleLed);
  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("HTTP server started on port 80");
}

// -------------------- Main --------------------

void setup() {
  Serial.begin(115200);
  delay(50);

  pinMode(SENSOR_PIN, INPUT);
  pinMode(LED_BUILTIN, OUTPUT);
  setLed(false);

  if (loadConfig(config)) {
    Serial.println("Saved config found:");
    Serial.print("SSID: ");
    Serial.println(config.ssid);
    Serial.print("Hostname: ");
    Serial.println(config.hostname);
    Serial.print("Trigger condition: ");
    Serial.println(getConfiguredTriggerCondition());

    if (loadServerIpFromConfig()) {
      Serial.print("Server IP: ");
      Serial.println(SERVER_IP.toString());
    }

    startNormalMode();
  } else {
    Serial.println("No saved config found");
    startSetupMode();
  }
}

void loop() {
  server.handleClient();
  MDNS.update();
  processTrigger();
}