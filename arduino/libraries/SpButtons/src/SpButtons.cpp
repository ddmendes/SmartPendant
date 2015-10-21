#include <SpButtons.h>
#include <Arduino.h>

SpButtons::SpButtons(int pinBtnLeftTop, int pinBtnLeftBottom,
                     int pinBtnRightTop, int pinBtnRightBottom,
                     int btnActiveState)
                     : bltPin{pinBtnLeftTop}, blbPin{pinBtnLeftBottom},
                       brtPin{pinBtnRightTop}, brbPin{pinBtnRightBottom},
                       activeState{btnActiveState}
{
    lastReading = 0;
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

char* SpButtons::getJson() {
    buttonState &= ~NEWVALUE_MASK;
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
            return "none";
    }
}
