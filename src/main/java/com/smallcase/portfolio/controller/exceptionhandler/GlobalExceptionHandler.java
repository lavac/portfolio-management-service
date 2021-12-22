package com.smallcase.portfolio.controller.exceptionhandler;

import com.smallcase.portfolio.controller.dto.ErrorResponse;
import com.smallcase.portfolio.exception.*;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e1) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e1);
    log.error(String.format("Invalid input: %s ", rootCauseMessage));
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse("Invalid input", rootCauseMessage));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Invalid input: %s ", rootCauseMessage));
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse("Invalid input", rootCauseMessage));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Invalid input: %s ", rootCauseMessage));
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse("Invalid input", rootCauseMessage));
  }

  @ExceptionHandler(InvalidTradeOperation.class)
  public ResponseEntity<ErrorResponse> handle(InvalidTradeOperation e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Bad Request: %s ", rootCauseMessage));
    return ResponseEntity.status(CONFLICT)
        .body(new ErrorResponse("Invalid trade operation", rootCauseMessage));
  }

  @ExceptionHandler(InvalidTradeTypeException.class)
  public ResponseEntity<ErrorResponse> handle(InvalidTradeTypeException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Invalid trade type: %s, valid types are BUY and SELL! ", rootCauseMessage));
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse(e.getMessage(), rootCauseMessage));
  }

  @ExceptionHandler(NoPortfolioFoundException.class)
  public ResponseEntity<ErrorResponse> handle(NoPortfolioFoundException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Portfolio not found : %s ", rootCauseMessage));
    return ResponseEntity.status(NOT_FOUND)
        .body(new ErrorResponse("Portfolio id not found", rootCauseMessage));
  }

  @ExceptionHandler(SecuritySellException.class)
  public ResponseEntity<ErrorResponse> handle(SecuritySellException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Cannot sell this security : %s ", rootCauseMessage));
    return ResponseEntity.status(CONFLICT)
        .body(new ErrorResponse("Cannot sell this security", rootCauseMessage));
  }

  @ExceptionHandler(TradeIdNotFoundException.class)
  public ResponseEntity<ErrorResponse> handle(TradeIdNotFoundException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Trade id not found : %s ", rootCauseMessage));
    return ResponseEntity.status(NOT_FOUND)
        .body(new ErrorResponse("Trade id not found", rootCauseMessage));
  }

  @ExceptionHandler(TradeUpdateException.class)
  public ResponseEntity<ErrorResponse> handle(TradeUpdateException e) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
    log.error(String.format("Cannot do this update operation: %s ", rootCauseMessage));
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse(String.format("Update operation failed with error message: %s", e.getMessage()),
            rootCauseMessage));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception ex) {
    String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
    log.error(String.format("Unexpected error occurred : %s ", rootCauseMessage));
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(String.format("Unexpected error occurred : %s", ex.getMessage()),
            rootCauseMessage));
  }
}
