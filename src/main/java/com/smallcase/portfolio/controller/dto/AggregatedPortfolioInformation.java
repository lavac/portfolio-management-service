package com.smallcase.portfolio.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AggregatedPortfolioInformation {
  private String tickerSymbol;
  private Double averageBuyPrice;
  private Integer shares;
}
