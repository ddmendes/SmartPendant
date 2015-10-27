#include <SpBluetooth.h>
#include <Arduino.h>

SpBluetooth::SpBluetooth(SoftwareSerial& s, char* inputBuffer)
    : serial(s), inputBuffer(inputBuffer) {
  inputUsage = 0;
}

void SpBluetooth::begin(int baudRate) {
  serial.begin(baudRate);
}

bool SpBluetooth::loop() {
  int l = serial.available();

  if(l == 0) {
    return NULL;
  }

  l = serial.readBytes(&(inputBuffer[inputUsage]), l);
  inputUsage += l;

  if(inputBuffer[inputUsage - 1] == '\n') {
    inputBuffer[inputUsage - 1] = '\0';
    String s = inputBuffer;
    Serial.println(s);
    inputUsage = 0;

    return &s;
  }

  return NULL;
}

void SpBluetooth::write(const char* message) {
  serial.println(message);
}
