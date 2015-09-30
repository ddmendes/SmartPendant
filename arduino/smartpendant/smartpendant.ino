#include <SoftwareSerial.h>

#define RED_PIN   5
#define GREEN_PIN 6
#define BLUE_PIN  9
#define VIBRA_PIN 3

#define VIBRA_DURATION 200

SoftwareSerial bt(2, 4);
char op = NULL;
bool vibra = false;
long lastVibra = 0;
byte vibraState = -255;

void setup() {
  pinMode(RED_PIN  , OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN , OUTPUT);
  pinMode(VIBRA_PIN, OUTPUT);
  bt.begin(38400);

  analogWrite(VIBRA_PIN, 255);
  delay(VIBRA_DURATION);
  analogWrite(VIBRA_PIN, 0);
  delay(VIBRA_DURATION);
  analogWrite(VIBRA_PIN, 255);
  delay(VIBRA_DURATION);
  analogWrite(VIBRA_PIN, 0);
}

void loop() {
  if(bt.available() > 0) {
    op = bt.read();
    analogWrite(255 * (op == 'r'), RED_PIN);
    analogWrite(255 * (op == 'g'), GREEN_PIN);
    analogWrite(255 * (op == 'b'), BLUE_PIN);
    vibra = true;
  }

  if(vibra && (millis() - lastVibra > VIBRA_DURATION)) {
    vibraState *= -1;
    byte vibraLvl = analogRead(VIBRA_PIN);
    analogWrite(VIBRA_PIN, vibraLvl + vibraState);
  }
}
