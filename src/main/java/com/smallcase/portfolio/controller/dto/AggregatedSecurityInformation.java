package com.smallcase.portfolio.controller.dto;

import com.smallcase.portfolio.repository.entity.Trade;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AggregatedSecurityInformation {
  private String tickerSymbol;
  private List<Trade> trades;
}
