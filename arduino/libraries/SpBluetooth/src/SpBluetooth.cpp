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
    return false;
  }

  l = serial.readBytes(&(inputBuffer[inputUsage]), l);
  inputUsage += l;

  if(inputBuffer[inputUsage - 1] == '\n') {
    inputBuffer[inputUsage - 1] = '\0';
    inputUsage = 0;

    return true;
  }

  return false;
}

void SpBluetooth::write(const char* message) {
  serial.println(message);
}
