#ifndef _SP_BLUETOOTH_H_
#define _SP_BLUETOOTH_H_

#include <SoftwareSerial.h>

class SpBluetooth {
public:
  SpBluetooth(SoftwareSerial& s, char* inputBuffer);
  void begin(int baudRate);
  bool loop();
  void write(const char* message);
private:
  SoftwareSerial& serial;
  char* inputBuffer;
  int inputUsage;
};

#endif