#ifndef _SP_ACTUATION_H_
#define _SP_ACTUATION_H_

#include <ArduinoJson.h>

#define LED_TARGET ("led")
#define VIBRA_TARGET ("vibra")

class SpRgbLed {
public:
  SpRgbLed(int redPin, int greenPin, int bluePin);
  void actuate(const JsonObject& act);
  void loop();
private:
  struct ledState {
    int red;
    int green;
    int blue;
    int duration;
  } state[10];
  int steps;
  int position;
  long lastStep;

  int redPin;
  int greenPin;
  int bluePin;
};

class SpVibra {
public:
  SpVibra(int pin);
  void actuate(const JsonObject& act);
  void loop();
private:
  struct vibraState {
    int level;
    int duration;
  } state[10];
  int steps;
  int position;
  long lastStep;

  int pin;
};

class SpActuation {
public:
  SpActuation(SpRgbLed& led, SpVibra& vibra);
  void actuate(const JsonObject& act);
  void loop();
private:
  SpRgbLed& led;
  SpVibra& vibra;
};

#endif