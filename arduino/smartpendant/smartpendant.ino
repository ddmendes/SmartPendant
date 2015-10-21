#include <SoftwareSerial.h>
#include <SpButtons.h>

// Output pins
#define RED_PIN   5
#define GREEN_PIN 6
#define BLUE_PIN  9
#define VIBRA_PIN 3

// Input pins
#define PBUTTON_PIN 8
#define LTBUTTON_PIN 13
#define LBBUTTON_PIN 12
#define RTBUTTON_PIN 11
#define RBBUTTON_PIN 10

#define VIBRA_DURATION 200

SoftwareSerial bt(4, 2);
SpButtons btns(LTBUTTON_PIN, LBBUTTON_PIN, RTBUTTON_PIN, RBBUTTON_PIN, LOW);
char op = NULL;
bool vibra = false;
bool doubleBlink = false;
long lastVibra;

void setup() {
  // Pin modes.
  pinMode(RED_PIN  , OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN , OUTPUT);
  pinMode(VIBRA_PIN, OUTPUT);
  pinMode(PBUTTON_PIN, INPUT_PULLUP);
  pinMode(LTBUTTON_PIN, INPUT_PULLUP);
  pinMode(LBBUTTON_PIN, INPUT_PULLUP);
  pinMode(RTBUTTON_PIN, INPUT_PULLUP);
  pinMode(RBBUTTON_PIN, INPUT_PULLUP); 

  // Bluetooth serial initialization.
  bt.begin(9600);

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
  btns.checkButtons();
  if(btns.hasButtonsToRead()) {
    Serial.println(btns.getJson());
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
