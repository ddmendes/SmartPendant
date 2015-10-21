#ifndef _SP_BUTTONS_H_
#define _SP_BUTTONS_H_

#define NEWVALUE_MASK  0b10000
#define LTBUTTON_MASK  0b01000
#define LBBUTTON_MASK  0b00100
#define RBBUTTON_MASK  0b00010
#define RTBUTTON_MASK  0b00001
#define DBLTOP_MASK    (LTBUTTON_MASK & RTBUTTON)
#define DBLBOTTOM_MASK (LBBUTTON_MASK & RBBUTTON)
#define DBLLEFT_MASK   (LTBUTTON_MASK & LBBUTTON)
#define DBLRIGHT_MASK  (RTBUTTON_MASK & RBBUTTON)
#define LTBUTTON_NAME  "left_top"
#define LBBUTTON_NAME  "left_bottom"
#define RTBUTTON_NAME  "right_top"
#define RBBUTTON_NAME  "right_bottom"
#define DBLTOP_NAME    "double_top"
#define DBLBOTTOM_NAME "double_bottom"
#define DBLLEFT_NAME   "double_left"
#define DBLRIGHT_NAME  "double_right"

class SpButtons {
public:
    SpButtons(int8_t pinBtnLeftTop, int8_t pinBtnLeftBottom,
              int8_t pinBtnRightTop, int8_t pinBtnRightBottom,
              int btnActiveState);
    void checkButtons();
    bool hasButtonsToRead();
    char* getJson();
private:
    uint8_t buttonState;
    int activeState;
    int8_t bltPin;
    int8_t blbPin;
    int8_t brtPin;
    int8_t brbPin;
};

#endif