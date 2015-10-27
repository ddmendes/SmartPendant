#ifndef _SP_ACTUATION_H_
#define _SP_ACTUATION_H_

#include <ArduinoJson.h>

#define LED_TARGET ("led")
#define VIBRA_TARGET ("vibra")

class SpActuation {
public:
	SpActuation(SpRgbLed& led, SpVibra& vibra);
	void actuate(JsonObject& act);
	void loop();
private:
	SpRgbLed& led;
	SpVibra& vibra;
};

#endif