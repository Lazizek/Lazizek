#include <WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// Optional sensor includes:
// #include <PZEM004Tv30.h>
// #include <HLW8012.h>

const char* WIFI_SSID = "YOUR_WIFI";
const char* WIFI_PASS = "YOUR_PASS";
const char* MQTT_HOST = "192.168.1.100";
const int   MQTT_PORT = 1883;
const char* MQTT_USER = "user";
const char* MQTT_PASS = "pass";

const char* HOME_ID = "demo";
const char* DEVICE_ID = "plug-1";
const int RELAY_PIN = 23;

WiFiClient wifiClient;
PubSubClient mqtt(wifiClient);
bool relayOn = false;
unsigned long lastTelemetryMs = 0;

String topicCommand() { return "home/" + String(HOME_ID) + "/device/" + DEVICE_ID + "/command"; }
String topicState() { return "home/" + String(HOME_ID) + "/device/" + DEVICE_ID + "/state"; }
String topicTelemetry() { return "home/" + String(HOME_ID) + "/device/" + DEVICE_ID + "/telemetry"; }
String topicLwt() { return "home/" + String(HOME_ID) + "/device/" + DEVICE_ID + "/lwt"; }

void publishState() {
  StaticJsonDocument<128> doc;
  doc["on"] = relayOn;
  doc["relay"] = relayOn ? 1 : 0;
  doc["ts"] = millis() / 1000;
  char out[128];
  serializeJson(doc, out);
  mqtt.publish(topicState().c_str(), out, true);
}

void publishTelemetry() {
  // Replace mock values with PZEM/HLW readings
  float voltage = 220.4;
  float current = relayOn ? 0.31 : 0.0;
  float power = voltage * current;
  static float energy = 1.42;
  if (relayOn) energy += power / 3600000.0 * 5000.0;

  StaticJsonDocument<192> doc;
  doc["voltage"] = voltage;
  doc["current"] = current;
  doc["power"] = power;
  doc["energy"] = energy;
  doc["freq"] = 50.0;
  doc["pf"] = 0.91;
  doc["ts"] = millis() / 1000;
  char out[192];
  serializeJson(doc, out);
  mqtt.publish(topicTelemetry().c_str(), out, false);
}

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  String data;
  for (unsigned int i = 0; i < length; i++) data += (char)payload[i];

  StaticJsonDocument<128> doc;
  if (deserializeJson(doc, data) == DeserializationError::Ok) {
    if (String(doc["cmd"].as<const char*>()) == "set") {
      relayOn = doc["on"] | false;
      digitalWrite(RELAY_PIN, relayOn ? HIGH : LOW);
      publishState();
    }
  }
}

void connectWifi() {
  if (WiFi.status() == WL_CONNECTED) return;
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  while (WiFi.status() != WL_CONNECTED) delay(500);
}

void connectMqtt() {
  if (mqtt.connected()) return;
  String clientId = String("esp32-") + DEVICE_ID;
  String lwtPayload = "{\"online\":false,\"ts\":0}";

  while (!mqtt.connected()) {
    if (mqtt.connect(clientId.c_str(), MQTT_USER, MQTT_PASS, topicLwt().c_str(), 1, true, lwtPayload.c_str())) {
      mqtt.subscribe(topicCommand().c_str());
      mqtt.publish(topicLwt().c_str(), "{\"online\":true,\"ts\":1}", true);
      publishState();
    } else {
      delay(2000);
    }
  }
}

void setup() {
  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, LOW);

  connectWifi();
  mqtt.setServer(MQTT_HOST, MQTT_PORT);
  mqtt.setCallback(mqttCallback);
  connectMqtt();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) connectWifi();
  if (!mqtt.connected()) connectMqtt();
  mqtt.loop();

  if (millis() - lastTelemetryMs >= 5000) {
    lastTelemetryMs = millis();
    publishTelemetry();
  }
}
