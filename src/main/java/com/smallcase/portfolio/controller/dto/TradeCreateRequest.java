package com.smallcase.portfolio.controller.dto;

import lombok.Data;

@Data
public class TradeCreateRequest {
  private String tradeType;
  private Double pricePerShare;
  private Integer numberOfShares;
  private String tickerSymbol;
}
