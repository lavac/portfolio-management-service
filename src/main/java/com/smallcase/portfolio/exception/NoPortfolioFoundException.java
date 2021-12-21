package com.smallcase.portfolio.exception;

public class NoPortfolioFoundException extends RuntimeException{
  public NoPortfolioFoundException(String message) {
    super(message);
  }
}
