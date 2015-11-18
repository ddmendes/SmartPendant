#include <SpActuation.h>
#include <Arduino.h>
#include <string.h>

SpActuation::SpActuation(SpRgbLed& led, SpVibra& vibra)
    : led(led), vibra(vibra) {}

void SpActuation::actuate(const JsonObject& act) {
  int actuations = act["acts"].size();
  Serial.print("actuations: ");
  Serial.println(actuations);

  JsonArray& acts = act["acts"];
  Serial.println(acts.size());
  Serial.println((const char *) act["acts"][0]["target"]);
  Serial.println((const char *) act["acts"][1]["target"]);

  for(int i = 0; i < actuations; i++) {
    JsonObject& actuate = act["acts"][i];
    const char* target = actuate["target"];
    Serial.print("target: ");
    Serial.println(target);
    
    if(strcmp(target, LED_TARGET) == 0) {
      Serial.println("Dispatching to led");
      led.actuate(actuate);
    } else if(strcmp(target, VIBRA_TARGET) == 0) {
      Serial.println("Dispatching to vibra");
      vibra.actuate(actuate);
    }
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
  steps = act["states"].size();
  position = 0;

  for(int i = 0; i < steps; ++i) {
    state[i].red = act["states"][i]["val"]["r"];
    state[i].green = act["states"][i]["val"]["g"];
    state[i].blue = act["states"][i]["val"]["b"];
    state[i].duration = act["states"][i]["dur"];
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
  steps = act["states"].size();
  position = 0;

  for(int i = 0; i < steps; ++i) {
    state[i].level = act["states"][i]["val"];
    state[i].duration = act["states"][i]["dur"];
  }
}

void SpVibra::loop() {
  if(steps == 0) {
    return;
  }

  if(position == 0) {
    digitalWrite(pin, state[position].level == 1 ? HIGH : LOW);
    lastStep = millis();
    ++position;
  } else if(millis() - lastStep > state[position - 1].duration) {
    digitalWrite(pin, state[position].level == 1 ? HIGH : LOW);
    if(++position >= steps) {
      position = 0;
      steps = 0;
      return;
    } else {
      lastStep = millis();
    }
  }
}