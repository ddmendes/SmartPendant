#define BUTTON_INPUT A0
#define RED_PIN   5
#define GREEN_PIN 6
#define BLUE_PIN  9

enum Button {
  NO_BUTTON,
  LEFT_TOP,
  LEFT_MIDDLE,
  LEFT_BOTTOM,
  RIGHT_TOP,
  RIGHT_MIDDLE,
  RIGHT_BOTTOM
} btn = NO_BUTTON;

void checkButtons();

void setup() {
  Serial.begin(9600);
  pinMode(RED_PIN, OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN, OUTPUT);
}

void loop() {
  float r;
  float g;
  float b;
  long start;

  static Button lastButton = NO_BUTTON;
  checkButtons();

  if(btn != NO_BUTTON && lastButton == NO_BUTTON) {
    switch (btn) {
        case LEFT_TOP:
          Serial.println("btnLeftTop");
          for(int i = 0; i < 256; i++) {
            analogWrite(RED_PIN, i);
            delay(10);
          }
          for(int i = 255; i > -1; i--) {
            analogWrite(RED_PIN, i);
            delay(10);
          }
          break;
        case LEFT_MIDDLE:
          Serial.println("btnLeftMiddle");
          for(int i = 0; i < 256; i++) {
            analogWrite(GREEN_PIN, i);
            delay(10);
          }
          for(int i = 255; i > -1; i--) {
            analogWrite(GREEN_PIN, i);
            delay(10);
          }
          break;
        case LEFT_BOTTOM:
          Serial.println("btnLeftBottom");
          for(int i = 0; i < 256; i++) {
            analogWrite(BLUE_PIN, i);
            delay(10);
          }
          for(int i = 255; i > -1; i--) {
            analogWrite(BLUE_PIN, i);
            delay(10);
          }
          break;
        case RIGHT_TOP:
          Serial.println("btnRightTop");
          analogWrite(RED_PIN, 255);
          delay(500);
          analogWrite(GREEN_PIN, 255);
          delay(500);
          analogWrite(RED_PIN, 0);
          delay(500);
          analogWrite(BLUE_PIN, 255);
          delay(500);
          analogWrite(GREEN_PIN, 0);
          delay(500);
          analogWrite(BLUE_PIN, 0);
          break;
        case RIGHT_MIDDLE:
          Serial.println("btnRightMiddle");
          r = 0;
          g = 3.1415 * 15 / 180;
          b = 3.1415 * 30 / 180;
          start = millis();
          while(millis() - start < 5000) {
            analogWrite(RED_PIN, 255 * 0.5 * (sin(r) + 1));
            analogWrite(GREEN_PIN, 255 * 0.5 * (sin(g) + 1));
            analogWrite(BLUE_PIN, 255 * 0.5 * (sin(b) + 1));
            delay(10);
            r += 0.1;
            g += 0.1;
            b += 0.1;
          }
          break;
        case RIGHT_BOTTOM:
          Serial.println("btnRightBottom");
          r = 0;
          g = 3.1415 * 30 / 180;
          b = 3.1415 * 60 / 180;
          start = millis();
          while(millis() - start < 5000) {
            analogWrite(RED_PIN, 255 * 0.5 * (sin(r) + 1));
            analogWrite(GREEN_PIN, 255 * 0.5 * (sin(g) + 1));
            analogWrite(BLUE_PIN, 255 * 0.5 * (sin(b) + 1));
            delay(10);
            r += 0.1;
            g += 0.1;
            b += 0.1;
          }
          break;
    }
    analogWrite(RED_PIN, 0);
    analogWrite(GREEN_PIN, 0);
    analogWrite(BLUE_PIN, 0);
  }

  lastButton = btn;
}

void checkButtons() {
  register int bPin = analogRead(BUTTON_INPUT);

  if(937 < bPin) {
    btn = NO_BUTTON;
  } else if (835 < bPin && bPin <= 937) {
    btn = RIGHT_BOTTOM;
  } else if (793 < bPin && bPin <= 835) {
    btn = RIGHT_MIDDLE;
  } else if (725 < bPin && bPin <= 793) {
    btn = RIGHT_TOP;
  } else if (600 < bPin && bPin <= 725) {
    btn = LEFT_BOTTOM;
  } else if (282 < bPin && bPin <= 600) {
    btn = LEFT_MIDDLE;
  } else if (bPin < 282) {
    btn = LEFT_TOP;
  }
}
