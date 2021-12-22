package com.smallcase.portfolio.exception;

public class InvalidTradeOperation extends RuntimeException {
  public InvalidTradeOperation(String message) {
    super(message);
  }
}
