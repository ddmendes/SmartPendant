#include <SoftwareSerial.h>

#define RED_PIN   5
#define GREEN_PIN 6
#define BLUE_PIN  9
#define VIBRA_PIN 3

#define VIBRA_DURATION 200

SoftwareSerial bt(4, 2);
char op = NULL;
bool vibra = false;
long lastVibra;

void setup() {
  // Pin modes.
  pinMode(RED_PIN  , OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN , OUTPUT);
  pinMode(VIBRA_PIN, OUTPUT);

  // Bluetooth serial initialization.
  bt.begin(9600 );

  // Serial initialization for loggin
  Serial.begin(9600);
  Serial.println("SmartPendant software.");

  // Double vibration pulses.
  digitalWrite(VIBRA_PIN, HIGH);
  delay(VIBRA_DURATION);
  digitalWrite(VIBRA_PIN, LOW);
  delay(VIBRA_DURATION);
  digitalWrite(VIBRA_PIN, HIGH);
  delay(VIBRA_DURATION);
  digitalWrite(VIBRA_PIN, LOW);
}

void loop() {
  if(bt.available() > 0) {
    op = bt.read();

    Serial.print("Operation received: ");
    Serial.println(op);

    // digitalWrite(RED_PIN, (op == 'r') ? HIGH : LOW);
    // digitalWrite(GREEN_PIN, (op == 'g') ? HIGH : LOW);
    // digitalWrite(BLUE_PIN, (op == 'b') ? HIGH : LOW);
    analogWrite(RED_PIN, (op == 'r') * 255);
    analogWrite(GREEN_PIN, (op == 'g') * 255);
    analogWrite(BLUE_PIN, (op == 'b') * 255);

    vibra = true;
  }

  if(vibra) {
    lastVibra = millis();
    digitalWrite(VIBRA_PIN, HIGH);
    vibra = false;
  }

  if(!vibra && (millis() - lastVibra > VIBRA_DURATION)) {
    digitalWrite(VIBRA_PIN, LOW);
  }
}
