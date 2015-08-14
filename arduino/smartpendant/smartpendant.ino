#include <Message.h>
#include <util/atomic.h>

#define BTN2 4
#define BTN1 7
#define BTN0 8

#define NO_BUTTON 0B111

#define BTN_LFT_TOP 0b010
#define BTN_LFT_MID 0b001
#define BTN_LFT_BOT 0b000

#define BTN_RGT_TOP 0b110
#define BTN_RGT_MID 0b101
#define BTN_RGT_BOT 0b100

void buttonPressed();
void panicButton();

int button = NO_BUTTON;
bool panic = false;
char payload[1000];
struct ButtonEvent {
  char* type = "button";
  char* source;
  char* state = "click";
};

void setup() {
  pinMode(BTN2, INPUT_PULLUP);
  pinMode(BTN1, INPUT_PULLUP);
  pinMode(BTN0, INPUT_PULLUP);
  pinMode(13, OUTPUT);
  // attachInterrupt(0, buttonPressed, FALLING);
  // attachInterrupt(1, panicButton, FALLING);
  Serial.begin(9600);
  Serial.println("oi");
}

void loop() {
  button  = 0;
  button |= digitalRead(BTN2) == HIGH ? 0b100 : 0;
  button |= digitalRead(BTN1) == HIGH ? 0b010 : 0;
  button |= digitalRead(BTN0) == HIGH ? 0b001 : 0;


  int btn;
  //ATOMIC_BLOCK(ATOMIC_FORCEON) {
    btn = button;
  //}

  bool pnc;
  //ATOMIC_BLOCK(ATOMIC_FORCEON) {
    pnc = panic;
  //}

  if(btn < NO_BUTTON) {
    digitalWrite(13, HIGH);
    delay(200);
    digitalWrite(13, LOW);
    delay(200);
    digitalWrite(13, HIGH);
    delay(200);
    digitalWrite(13, LOW);
    struct ButtonEvent event;
    switch (btn) {
        case BTN_LFT_TOP:
          event.source = "leftTopButton";
          Serial.println("leftTopButton");
          break;
        case BTN_LFT_MID:
          event.source = "leftMiddleButton";
          Serial.println("leftMiddleButton");
          break;
        case BTN_LFT_BOT:
          event.source = "leftBottomButton";
          Serial.println("leftBottomButton");
          break;
        case BTN_RGT_TOP:
          event.source = "rightTopButton";
          Serial.println("rightTopButton");
          break;
        case BTN_RGT_MID:
          event.source = "rightMiddleButton";
          Serial.println("rightMiddleButton");
          break;
        case BTN_RGT_BOT:
          event.source = "rightBottonButton";
          Serial.println("rightBottonButton");
          break;
        default:
          Serial.println("error");
          break;
    }

    MessageContext msg = MessageFactory::getEventMessage(event.type, event.source, event.state);
    msg.getMessage(payload, 1000);
    //Serial.println(payload);

    ATOMIC_BLOCK(ATOMIC_FORCEON) {
      button = NO_BUTTON;
    }
  }
}

void buttonPressed() {
  button  = 0;
  button |= digitalRead(BTN2) == HIGH ? 0b100 : 0;
  button |= digitalRead(BTN1) == HIGH ? 0b010 : 0;
  button |= digitalRead(BTN0) == HIGH ? 0b001 : 0;
}

void panicButton() {
  panic = true;
}
