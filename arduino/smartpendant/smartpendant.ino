#include <SoftwareSerial.h>
#include <SpButtons.h>
#include <SpBluetooth.h>
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
#define BUFFER_SIZE 300

char* outputBuffer = (char*) calloc(BUFFER_SIZE, sizeof(char));
char* inputBuffer = (char*) calloc(BUFFER_SIZE, sizeof(char));

SoftwareSerial softwareSerial(4, 2);
SpBluetooth bluetooth(softwareSerial, inputBuffer);
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
    btns.getJsonEvent(&(outputBuffer[1]), BUFFER_SIZE - 1);
    outputBuffer[0] = '{';
    strcat(outputBuffer, "}");
    bluetooth.println(outputBuffer);
  }

  if(bluetooth.loop()) {
    Serial.println(inputBuffer);
  }
}

/*
#define LED_TARGET ("led")
#define VIBRA_TARGET ("vibra")

void readBluetooth() {
  int l = bt.available();
  if(l > 0) {
    l = bt.readBytes(&(inputBuffer[inputSize]), l);
    inputSize += l;

    if(inputBuffer[inputSize - 1] == '\n') {
      inputBuffer[inputSize - 1] = '\0';
      Serial.println(inputBuffer);

      StaticJsonBuffer<300> jsonBuffer;
      JsonObject& root = jsonBuffer.parseObject(inputBuffer);
      JsonObject& actuation = root["actuation"];
      String target = actuation["target"];
      Serial.println(target);

      if(target.compareTo(LED_TARGET) == 0) {
        int steps = actuation["steps"];
        for(int i = 0; i < steps; i++) {
          digitalWrite(RED_PIN, (int) actuation["states"][i]["value"]["red"]);
          digitalWrite(GREEN_PIN, (int) actuation["states"][i]["value"]["green"]);
          digitalWrite(BLUE_PIN, (int) actuation["states"][i]["value"]["blue"]);
          delay((long) actuation["states"][i]["duration"]);
        }
      } else if(target.compareTo(VIBRA_TARGET) == 0) {
        int steps = actuation["steps"];
        for(int i = 0; i < steps; i++) {
          digitalWrite(VIBRA_PIN, (int) actuation["states"][i]["value"]);
          delay((long) actuation["states"][i]["duration"]);
        }
      }
      inputSize = 0;
    }
  }
}
*/