/**
 * @file Message.cpp
 * Library for writing smartpendant messages.
 * 
 * Copyright 2015 Davi Diorio Mendes
 * github.com/ddmendes
 */
#include <Message.h>

int copyUpTo(char* target, char* source, size_t limit,
             bool endWithSemicolon = false) {
  int i;
  for(i = 0; source[i] != '\0' && i < limit; i++) {
    target[i] = source[i];
  }

  if(i < limit) {
    target[i++] = ';';
  }

  if(i < limit) {
    target[i] = '\0';
  }

  return i;
}

MessageContext::MessageContext(PayloadStrategy* payload) : mPayload(payload) {}

int MessageContext::getMessage(char* msgBuffer, int bufferSize) {
  int nextPosition = 0;
  nextPosition += copyUpTo(msgBuffer, "spmsg;", bufferSize);
  nextPosition += mPayload->getPayloadType(&msgBuffer[nextPosition],
                                   bufferSize - nextPosition);
  nextPosition += mPayload->getPayloadData(&msgBuffer[nextPosition],
                                   bufferSize - nextPosition);
  return nextPosition;
}

EventPayloadStrategy::EventPayloadStrategy(char* type, char* source, 
                                           char* state) :
  mType(type),
  mSource(source),
  mState(state) {}

int EventPayloadStrategy::getPayloadType(char* buffer, int bufferSize) {
  return copyUpTo(buffer, "event", bufferSize, true);
}

int EventPayloadStrategy::getPayloadData(char* buffer, int bufferSize) {
  int nextPosition = 0;
  nextPosition += copyUpTo(buffer, mType, bufferSize, true);
  nextPosition += copyUpTo(&buffer[nextPosition], mSource,
                           bufferSize - nextPosition, true);
  nextPosition += copyUpTo(&buffer[nextPosition], mState,
                           bufferSize - nextPosition, true);
  return nextPosition;
}

MessageContext& MessageFactory::getEventMessage(char* type, char* source, 
                                                       char* state) {
  EventPayloadStrategy payload(type, source, state);
  MessageContext message(payload);
  return message;
}
