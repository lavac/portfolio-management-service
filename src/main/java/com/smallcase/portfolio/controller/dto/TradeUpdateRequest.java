package com.smallcase.portfolio.controller.dto;

import lombok.Data;

@Data
public class TradeUpdateRequest {
  private String tradeType;
  private Double pricePerShare;
  private Integer numberOfShares;
}
