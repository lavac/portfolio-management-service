package com.smallcase.portfolio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioManagementController {

  @GetMapping("/")
  public ResponseEntity<String> get() {
    return ResponseEntity.accepted().body("Portfolio dummy endpoint");
  }
}
