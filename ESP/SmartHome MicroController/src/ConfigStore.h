#ifndef CONFIGSTORE_H
#define CONFIGSTORE_H

#include <EEPROM.h>
#include <WString.h>

#define EEPROM_SIZE 256

struct WifiConfig {
  char marker[4];       // "CFG"
  char ssid[32];
  char password[64];
  char hostname[32];
};

inline void clearConfig() {
  WifiConfig config = {};
  EEPROM.begin(EEPROM_SIZE);
  EEPROM.put(0, config);
  EEPROM.commit();
  EEPROM.end();
}

inline bool loadConfig(WifiConfig &config) {
  EEPROM.begin(EEPROM_SIZE);
  EEPROM.get(0, config);
  EEPROM.end();

  return (
    config.marker[0] == 'C' &&
    config.marker[1] == 'F' &&
    config.marker[2] == 'G'
  );
}

inline bool saveConfig(const String &ssid, const String &password, const String &hostname) {
  WifiConfig config = {};

  config.marker[0] = 'C';
  config.marker[1] = 'F';
  config.marker[2] = 'G';
  config.marker[3] = '\0';

  ssid.toCharArray(config.ssid, sizeof(config.ssid));
  password.toCharArray(config.password, sizeof(config.password));
  hostname.toCharArray(config.hostname, sizeof(config.hostname));

  EEPROM.begin(EEPROM_SIZE);
  EEPROM.put(0, config);
  bool ok = EEPROM.commit();
  EEPROM.end();

  return ok;
}

#endif