package com.smallcase.portfolio.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ErrorResponse {

  private String message;
  private String rootCause;
  private LocalDateTime timeStamp;

  public ErrorResponse(String message, String rootCause) {
    this.message = message;
    this.rootCause = rootCause;
    this.timeStamp = LocalDateTime.now();
  }
}
