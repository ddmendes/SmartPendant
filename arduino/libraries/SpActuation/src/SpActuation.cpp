#include <SpActuation.h>

SpActuation::SpActuation(SpRgbLed& led, SpVibra& vibra)
    : led(led), vibra(vibra) {}

void SpActuation::actuate(JsonObject& act) {
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