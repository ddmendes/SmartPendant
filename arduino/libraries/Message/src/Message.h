/**
 * @file Message.h
 * Library for writing smartpendant messages.
 * 
 * Copyright 2015 Davi Diorio Mendes
 * github.com/ddmendes
 */

#ifndef MESSAGE_H
#define MESSAGE_H

#include "Arduino.h"

class PayloadStrategy {
public:
  /**
   * @brief Gets the payload type.
   * 
   * @param buffer A buffer to receive the event type.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  virtual int getPayloadType(char* buffer, int bufferSize) = 0;

  /**
   * @brief Gets the payload data.
   * 
   * @param buffer A buffer to receive the payload data.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  virtual int getPayloadData(char* buffer, int bufferSize) = 0;
};

class MessageContext {
private:
  PayloadStrategy* mPayload;
public:
  /**
   * @brief Full constructor.
   * 
   * Initializes the payload.
   * 
   * @param payload The message payload.
   */
  MessageContext(PayloadStrategy* payload);

  /**
   * @brief Gets the represented message.
   * 
   * @param msgBuffer A buffer to receive the message.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getMessage(char* msgBuffer, int bufferSize);
};

class EventPayloadStrategy : public PayloadStrategy {
private:
  char *mType;
  char *mSource;
  char *mState;
public:
  /**
   * @brief Full constructor.
   * 
   * Initializes all the parameters.
   * 
   * @param type The event type.
   * @param source The source of the event.
   * @param state The state of the source.
   */
  EventPayloadStrategy(char* type, char* source, char* state);

  /**
   * @brief Sets the event type.
   * 
   * @param type The event type.
   */
  void setType(char* type);

  /**
   * @brief Gets the event type.
   * 
   * @param buffer A buffer to receive the event type.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getType(char* buffer, int bufferSize);

  /**
   * @brief Sets the event source.
   * 
   * @param source The event source.
   */
  void setSource(char* source);

  /**
   * @brief Gets the event source.
   * 
   * @param buffer A buffer to receive the event source.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getSource(char* buffer, int bufferSize);

  /**
   * @brief Sets the source state when the event was triggered.
   * 
   * @param state The source state.
   */
  void setState(char* state);

  /**
   * @brief Gets the source state when the event was triggered.
   * 
   * @param buffer A buffer to receive the source state.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getState(char* buffer, int bufferSize);

  /**
   * @brief Gets the type of the payload
   * 
   * @param buffer A buffer to receive the data. Always write "event;".
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getPayloadType(char* buffer, int bufferSize);

  /**
   * @brief Serializes the event payload in the smartpendant message format.
   * 
   * @param buffer A buffer to receive the data.
   * @param bufferSize The available size in the buffer.
   * @return Bytes wrote to buffer.
   */
  int getPayloadData(char* buffer, int bufferSize);
};

class MessageFactory {
public:
  /**
   * @brief Factory method to construct the message.
   * 
   * @param type The event type.
   * @param source The source of the event.
   * @param state The state of the source.
   * @return The MessageContext object containing a event message.
   */
  static MessageContext& getEventMessage(char* type, char* source, char* state);
};

#endif
