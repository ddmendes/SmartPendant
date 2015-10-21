#include <SpButtons.h>
#include <Arduino.h>

SpButtons::SpButtons(int8_t pinBtnLeftTop, int8_t pinBtnLeftBottom,
              		 int8_t pinBtnRightTop, int8_t pinBtnRightBottom,
              		 int btnActiveState)
                     : bltPin{pinBtnLeftTop}, blbPin{pinBtnLeftBottom},
                       brtPin{pinBtnRightTop}, brbPin{pinBtnRightBottom},
                       activeState{btnActiveState}
{
	buttonState = 0;
}

void SpButtons::checkButtons() {
	register uint8_t tempBtnStt = 0;

	tempBtnStt |= digitalRead(bltPin) == activeState ? LTBUTTON_MASK : 0;
	tempBtnStt |= digitalRead(blbPin) == activeState ? LBBUTTON_MASK : 0;
	tempBtnStt |= digitalRead(brtPin) == activeState ? RTBUTTON_MASK : 0;
	tempBtnStt |= digitalRead(brbPin) == activeState ? RBBUTTON_MASK : 0;

	if(buttonState & 0xF != tempBtnStt) {
		buttonState = NEWVALUE_MASK & tempBtnStt;
	}
}

bool SpButtons::hasButtonsToRead() {
	return buttonState & NEWVALUE_MASK == NEWVALUE_MASK;
}

char* SpButtons::getJson() {
	buttonState |= ~NEWVALUE_MASK;
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
