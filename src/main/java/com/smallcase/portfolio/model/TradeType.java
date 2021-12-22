package com.smallcase.portfolio.model;

import com.smallcase.portfolio.exception.InvalidTradeTypeException;

public enum TradeType {
  BUY, SELL;

  public static TradeType getTradeType(String tradeType) {
    for(TradeType type : values()) {
      if(type.toString().equalsIgnoreCase(tradeType)) {
        return type;
      }
    }
    throw new InvalidTradeTypeException("Error :- Invalid trade type, valid types are BUY and SELL");
  }
}
