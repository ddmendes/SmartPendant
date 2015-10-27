#include <SpBluetooth.h>
#include <Arduino.h>

SpBluetoth::SpBluetooth(SoftwareSerial s, int inputBufferSize) : serial{s} {
  inputBuffer = (char*) calloc(inputBufferSize, sizeof(char));
  inputUsage = 0;
}

void SpBluetooth::begin(int baudRate) {
  serial.begin(baudRate);
}

const char * SpBluetooth::loop() {
  int l = serial.available();

  if(l == 0) {
    return NULL;
  }

  l = bt.readBytes(&(inputBuffer[inputUsage]), l);
  inputUsage += l;

  if(inputBuffer[inputUsage - 1] == '\n') {
    inputBuffer[inputUsage - 1] = '\0';
    String s = inputBuffer;
    inputUsage = 0;

    return s;
  }

  return NULL;
}
