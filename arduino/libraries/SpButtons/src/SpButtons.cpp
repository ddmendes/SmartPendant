#include <SpButtons.h>
#include <Arduino.h>
#include <stdio.h>


SpButtons::SpButtons(int pinBtnLeftTop, int pinBtnLeftBottom,
                     int pinBtnRightTop, int pinBtnRightBottom,
                     int btnActiveState)
                     : bltPin{pinBtnLeftTop}, blbPin{pinBtnLeftBottom},
                       brtPin{pinBtnRightTop}, brbPin{pinBtnRightBottom},
                       activeState{btnActiveState}
{
    buttonState = 0;
}

void SpButtons::checkButtons() {
    register unsigned int tempBtnStt = 0;

    tempBtnStt |= digitalRead(bltPin) == activeState ? LTBUTTON_MASK : 0;
    tempBtnStt |= digitalRead(blbPin) == activeState ? LBBUTTON_MASK : 0;
    tempBtnStt |= digitalRead(brtPin) == activeState ? RTBUTTON_MASK : 0;
    tempBtnStt |= digitalRead(brbPin) == activeState ? RBBUTTON_MASK : 0;

    buttonState = (buttonState & 0xF) != tempBtnStt && tempBtnStt > 0 ?
                  NEWVALUE_MASK : 0;
    buttonState |= tempBtnStt;
}

bool SpButtons::hasButtonsToRead() {
    return (buttonState & NEWVALUE_MASK) == NEWVALUE_MASK;
}

void SpButtons::getJsonEvent(char* buffer, int max_size) {
    buttonState &= ~NEWVALUE_MASK;

    snprintf(buffer, max_size,
        "\"event\": {\"type\": \"button-push\", \"length\": \"short\", \"source\": \"%s\"}",
        getPatternName());
}

char* SpButtons::getPatternName() {
    switch(buttonState) {
        case LTBUTTON_MASK:
            return LTBUTTON_NAME;
        case LBBUTTON_MASK:
            return LBBUTTON_NAME;
        case RTBUTTON_MASK:
            return RTBUTTON_NAME;
        case RBBUTTON_MASK:
            return RBBUTTON_NAME;
        case DBLTOP_MASK:
            return DBLTOP_NAME;
        case DBLBOTTOM_MASK:
            return DBLBOTTOM_NAME;
        case DBLLEFT_MASK:
            return DBLLEFT_NAME;
        case DBLRIGHT_MASK:
            return DBLRIGHT_NAME;
        default:
            return NULL;
    }
}
