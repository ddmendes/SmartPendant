#ifndef _SP_BLUETOOTH_H_
#define _SP_BLUETOOTH_H_

class SpBluetooth {
public:
  SpBluetooth(int receivePin, int transferPin);
  void begin(int baudRate);
  const char* loop();
private:
  SoftwareSerial serial;
  char* inputBuffer;
  int inputUsage;
};

#endif