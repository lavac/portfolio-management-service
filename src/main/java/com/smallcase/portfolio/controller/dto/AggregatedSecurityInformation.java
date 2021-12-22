package com.smallcase.portfolio.controller.dto;

import com.smallcase.portfolio.model.TradeModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AggregatedSecurityInformation {
  private String tickerSymbol;
  private List<TradeModel> trades;
}
