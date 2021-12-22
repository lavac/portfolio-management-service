package com.smallcase.portfolio.model;

import lombok.Data;

@Data
public class TradeModel {
  private Integer id;
  private String tradeType;
  private Double price;
  private Integer numberOfShares;
}
