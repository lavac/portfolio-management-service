package com.smallcase.portfolio.exception;

public class TradeIdNotFoundException extends RuntimeException {
  public TradeIdNotFoundException(String message) {
    super(message);
  }
}
