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

// Button index
#define LTBUTTON 0
#define LBBUTTON 1
#define RTBUTTON 2
#define RBBUTTON 3

#define VIBRA_DURATION 200

/**
 * @brief Maps all pressed buttons to a boolean array.
 * 
 * <ul>
 * <li>If the LEFT TOP BUTTON is pushed, so btnState[0] is true.</li>
 * <li>If the LEFT BOTTOM BUTTON is pushed, so btnState[1] is true.</li>
 * <li>If the RIGHT TOP BUTTON is pushed, so btnState[2] is true.</li>
 * <li>If the RIGHT BOTTOM BUTTON is pushed, so btnState[3] is true.</li>
 * </ul>
 * 
 * @param btnState A bool array to receive the button states.
 * @return Returns true if at least one button is pushed.
 */
bool checkButtons(bool btnState[4]);

/**
 * @brief Generate the button string representation based on the button state
 * representation.
 * 
 * <ul>
 * <li>If btnState is [true, false, false, false], the button is "left-top".</li>
 * <li>If btnState is [false, true, false, false], the button is "left-bottom".</li>
 * <li>If btnState is [false, false, true, false], the button is "right-top".</li>
 * <li>If btnState is [false, false, false, true], the button is "right-bottom".</li>
 * <li>If btnState is [true, false, true, false], the button is "double-top".</li>
 * <li>If btnState is [false, true, false, true], the button is "double-bottom".</li>
 * <li>If btnState is [true, true, false, false], the button is "double-left".</li>
 * <li>If btnState is [false, false, true, true], the button is "double-right".</li>
 * <li>If none of the patterns, then the method returns <strong>NULL</strong>.
 * </ul>
 * 
 * @param btnState The bool button state representation.
 * @return The button string representation of the actual button state representation.
 */
char* getButton(const bool btnState[4]);

SoftwareSerial bt(4, 2);
char op = NULL;
bool vibra = false;
bool doubleBlink = false;
long lastVibra;
bool buttonState[] = {false, false, false, false};

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
  bool btnPushed = checkButtons(buttonState);
  if(btnPushed) {
    Serial.println(getButton(buttonState));
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

bool checkButtons(bool btnState[4]) {
  btnState[LTBUTTON] = digitalRead(LTBUTTON_PIN) == LOW;
  btnState[LBBUTTON] = digitalRead(LBBUTTON_PIN) == LOW;
  btnState[RTBUTTON] = digitalRead(RTBUTTON_PIN) == LOW;
  btnState[RBBUTTON] = digitalRead(RBBUTTON_PIN) == LOW;

  return btnState[LTBUTTON] ||
         btnState[LBBUTTON] ||
         btnState[RTBUTTON] ||
         btnState[RBBUTTON];
}

char* getButton(const bool btnState[4]) {
  
}
