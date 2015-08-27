#include <Message.h>
#include <util/atomic.h>

#define LAST_BTN_STATE_MASK 0b10101010
#define BTN_LFT_TOP_BIT 0b01000000
#define BTN_LFT_BOT_BIT 0b00010000
#define BTN_RGT_TOP_BIT 0b00000100
#define BTN_RGT_BOT_BIT 0b00000001
#define BTN_LFT_TOP_MASK 0b11000000
#define BTN_LFT_BOT_MASK 0b00110000
#define BTN_RGT_TOP_MASK 0b00001100
#define BTN_RGT_BOT_MASK 0b00000011
#define BTN_LFT_TOP_PIN 3
#define BTN_LFT_BOT_PIN 4
#define BTN_RGT_TOP_PIN 7
#define BTN_RGT_BOT_PIN 8
#define BTN_PANIC 2

void buttonPressed();
void panicButton();

int button;
char payload[1000];
struct ButtonEvent {
  char* type = "button";
  char* source;
  char* state = "click";
};

void setup() {
  pinMode(BTN_LFT_TOP_PIN, INPUT_PULLUP);
  pinMode(BTN_LFT_BOT_PIN, INPUT_PULLUP);
  pinMode(BTN_RGT_TOP_PIN, INPUT_PULLUP);
  pinMode(BTN_RGT_BOT_PIN, INPUT_PULLUP);
  pinMode(13, OUTPUT);
  // attachInterrupt(0, buttonPressed, FALLING);
  // attachInterrupt(1, panicButton, FALLING);
  Serial.begin(9600);
  Serial.println("oi");
}

void loop() {
  button <<= 1;
  button &= LAST_BTN_STATE_MASK;
  button |= digitalRead(BTN_LFT_TOP_PIN) == LOW ? BTN_LFT_TOP_BIT : 0;
  button |= digitalRead(BTN_LFT_BOT_PIN) == LOW ? BTN_LFT_BOT_BIT : 0;
  button |= digitalRead(BTN_RGT_TOP_PIN) == LOW ? BTN_RGT_TOP_BIT : 0;
  button |= digitalRead(BTN_RGT_BOT_PIN) == LOW ? BTN_RGT_BOT_BIT : 0;

  if(button > 0) {
    struct ButtonEvent event;
    switch (button) {
        case 0b1000:
          event.source = "leftTopButton";
          Serial.println("leftTopButton");
          break;
        case 0b0100:
          event.source = "leftBottomButton";
          Serial.println("leftBottomButton");
          break;
        case 0b0010:
          event.source = "rightTopButton";
          Serial.println("rightTopButton");
          break;
        case 0b0001:
          event.source = "rightBottonButton";
          Serial.println("rightBottonButton");
          break;
        default:
          Serial.println(button);
          break;
    }
  }
}
