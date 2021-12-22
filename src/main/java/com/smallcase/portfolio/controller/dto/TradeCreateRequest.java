package com.smallcase.portfolio.controller.dto;

import com.smallcase.portfolio.model.TradeType;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class TradeCreateRequest {
  private TradeType tradeType;
  @NonNull @DecimalMin(value = "1.0")
  private BigDecimal pricePerShare;
  @NonNull @Min(value = 1)
  private Integer numberOfShares;
}
