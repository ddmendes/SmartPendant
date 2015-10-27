#include <SpActuation.h>
#include <Arduino.h>

SpActuation::SpActuation(SpRgbLed& led, SpVibra& vibra)
    : led(led), vibra(vibra) {}

void SpActuation::actuate(const JsonObject& act) {
  String target = act["target"];

  if(target.compareTo(LED_TARGET) == 0) {
    led.actuate(act);
  } else if(target.compareTo(VIBRA_TARGET) == 0) {
    vibra.actuate(act);
  }
}

void SpActuation::loop() {
  led.loop();
  vibra.loop();
}

SpRgbLed::SpRgbLed(int redPin, int greenPin, int bluePin)
    : redPin(redPin), greenPin(greenPin), bluePin(bluePin) {
  lastStep = 0;
  position = 0;
}

void SpRgbLed::actuate(const JsonObject& act) {
  steps = act["steps"];
  position = 0;

  for(int i = 0; i < steps; ++i) {
    state[i].red = act["states"][i]["value"]["red"];
    state[i].green = act["states"][i]["value"]["green"];
    state[i].blue = act["states"][i]["value"]["blue"];
    state[i].duration = act["states"][i]["duration"];
  }
}

void SpRgbLed::loop() {
  if(steps == 0) {
    return;
  }

  if(position == 0) {
    analogWrite(redPin, (int) state[position].red);
    analogWrite(greenPin, (int) state[position].green);
    analogWrite(bluePin, (int) state[position].blue);
    lastStep = millis();
    ++position;
  } else if(millis() - lastStep > state[position - 1].duration) {
    analogWrite(redPin, (int) state[position].red);
    analogWrite(greenPin, (int) state[position].green);
    analogWrite(bluePin, (int) state[position].blue);
    if(++position >= steps) {
      position = 0;
      steps = 0;
      return;
    } else {
      lastStep = millis();
    }
  }
}

SpVibra::SpVibra(int pin) : pin(pin) {}

void SpVibra::actuate(const JsonObject& act) {
  steps = act["steps"];
  position = 0;

  for(int i = 0; i < steps; ++i) {
    state[i].level = act["states"][i]["value"];
    state[i].duration = act["states"][i]["duration"];
  }
}

void SpVibra::loop() {
  if(steps = 0) {
    return;
  }

  if(position == 0) {
    analogWrite(pin, state[position].level);
    lastStep = millis();
    ++position;
  } else if(millis() - lastStep > state[position - 1].duration) {
    analogWrite(pin, state[position].level);
    if(++position >= steps) {
      position = 0;
      steps = 0;
      return;
    } else {
      lastStep = millis();
    }
  }
}