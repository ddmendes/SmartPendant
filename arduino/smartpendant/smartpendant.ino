#include <SoftwareSerial.h>
#include <SpButtons.h>
#include <SpBluetooth.h>
#include <SpActuation.h>
#include <ArduinoJson.h>

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
#define INPUT_BUFFER_SIZE 400
#define OUTPUT_BUFFER_SIZE 110
#define JSON_BUFFER 550


char* outputBuffer = (char*) calloc(OUTPUT_BUFFER_SIZE, sizeof(char));
char* inputBuffer = (char*) calloc(INPUT_BUFFER_SIZE, sizeof(char));

SoftwareSerial softwareSerial(4, 2);
SpBluetooth bluetooth(softwareSerial, inputBuffer);
SpButtons btns(LTBUTTON_PIN, LBBUTTON_PIN, RTBUTTON_PIN, RBBUTTON_PIN, LOW);
SpRgbLed led(RED_PIN, GREEN_PIN, BLUE_PIN);
SpVibra vibra(VIBRA_PIN);
SpActuation spActuation(led, vibra);

char op = NULL;
bool doubleBlink = false;

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

  // Bluetooth initialization.
  bluetooth.begin(9600);

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
    btns.getJsonEvent(&(outputBuffer[1]), INPUT_BUFFER_SIZE - 1);
    outputBuffer[0] = '{';
    strcat(outputBuffer, "}");
    bluetooth.write(outputBuffer);
  }

  if(bluetooth.loop()) {
    Serial.println(inputBuffer);
    StaticJsonBuffer<JSON_BUFFER> jsonBuffer;
    JsonObject& msg = jsonBuffer.parseObject(inputBuffer); // "{\"acts\":[{\"t\":\"led\",\"s\":[{\"val\":{\"r\":0,\"g\":255,\"b\":255},\"dur\":200},{\"val\":{\"r\":0,\"g\":0,\"b\":0},\"dur\":200}]},{\"target\":\"vibra\",\"states\":[{\"val\": 1, \"dur\": 200},{\"val\": 0, \"dur\": 200}]}]}");

    if(!msg.success()) {
      Serial.println("deu pau");
    }

    // for (JsonObject::iterator it=msg.begin(); it!=msg.end(); ++it) {
    //   Serial.println(it->key);
    //   Serial.println(it->value.asString());
    // }
    spActuation.actuate(msg);
  }

  spActuation.loop();
}
