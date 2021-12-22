package com.smallcase.portfolio.controller.dto;

import com.smallcase.portfolio.model.TradeType;
import lombok.Data;

@Data
public class TradeUpdateRequest {
  private TradeType tradeType;
  private Double pricePerShare;
  private Integer numberOfShares;
}
