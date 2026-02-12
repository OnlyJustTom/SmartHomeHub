#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// ---------- WiFi config ----------
const char* WIFI_SSID     = "Tom";
const char* WIFI_PASSWORD = "a1234567";

// ---------- Web server ----------
ESP8266WebServer server(80);

#ifndef LED_BUILTIN
#define LED_BUILTIN 2
#endif

bool ledOn = false; // track logical state

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

void handleRoot() {
  server.send(200, "text/plain", "ok");
}

void handleStatus() {
  String body = "{";
  body += "\"chip\":\"ESP8266\",";
  body += "\"ip\":\"" + WiFi.localIP().toString() + "\",";
  body += "\"led\":" + String(ledOn ? "true" : "false");
  body += "}";
  server.send(200, "application/json", body);
}

void handleToggleLed() {
  if(server.method() != HTTP_POST) {
    server.send(405, "application/json", "{\"error\":\"Use POST\"}");
    return;
  }
  
  setLed(!ledOn);
  server.send(200, "application/json", String("{\"ok\":true,\"led\":") + (ledOn ? "true" : "false") + "}");
}

void handleNotFound() {
  String body = "{\"error\":\"not_found\",\"path\":\"" + jsonEscape(server.uri()) + "\"}";
  server.send(404, "application/json", body);
}

void setup() {
  Serial.begin(115200);
  delay(50);

  pinMode(LED_BUILTIN, OUTPUT);
  setLed(false);

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting to WiFi\n");
  while (WiFi.status() != WL_CONNECTED) {
    delay(300);
    Serial.print(".");
  }

  Serial.println();
  Serial.print("Connected! IP: ");
  Serial.println(WiFi.localIP());

  // Routes
  server.on("/", HTTP_GET, handleRoot);
  server.on("/api/status", HTTP_GET, handleStatus);
  server.on("/api/toggle", HTTP_POST, handleToggleLed);

  //Not found
  server.onNotFound(handleNotFound);
  server.begin();
  Serial.println("HTTP server started on port 80");
}

void loop() {
  server.handleClient();
}
