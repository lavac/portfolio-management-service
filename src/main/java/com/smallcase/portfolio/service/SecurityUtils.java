package com.smallcase.portfolio.service;

import com.smallcase.portfolio.exception.InvalidTradeOperation;
import com.smallcase.portfolio.exception.SecuritySellException;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import com.smallcase.portfolio.repository.entity.Trade;

public class SecurityUtils {
  public static final double MINIMUM_BUY_PRICE = 1.0;

  public static SecurityInfo recoverPreExistingSecurity(Trade existingTrade) {
    SecurityInfo security = existingTrade.getSecurity();
    if (existingTrade.getTradeType().equals("BUY")) {
      int preExistingNumberOfShares = getNumberOfSharesBeforeThisTrade(security.getNumberOfShares(),
          existingTrade.getNumberOfShares(), existingTrade.getTradeType());
      Double preExistingAveragePrice = getPreExistingAveragePriceForBuyType(existingTrade,
          preExistingNumberOfShares);
      if (preExistingAveragePrice <= 0.0 || preExistingNumberOfShares < 0) {
        throw new InvalidTradeOperation("Cannot update or remove this trade");
      }
      security.setNumberOfShares(preExistingNumberOfShares);
      security.setAveragePrice(preExistingAveragePrice);
    } else if (existingTrade.getTradeType().equals("SELL")) {
      int preExistingNumberOfShares = getNumberOfSharesBeforeThisTrade(security.getNumberOfShares(),
          existingTrade.getNumberOfShares(), existingTrade.getTradeType());
      security.setNumberOfShares(preExistingNumberOfShares);
    }
    return security;
  }

  public static Integer getNumberOfSharesBeforeThisTrade(Integer totalNumberOfShares, Integer numberOfSharesInThisTrade,
                                                   String tradeType) {
    return  (tradeType.equals("BUY"))
        ? (totalNumberOfShares - numberOfSharesInThisTrade)
        : totalNumberOfShares + numberOfSharesInThisTrade;
  }

  public static Double getPreExistingAveragePriceForBuyType(Trade existingTrade, int preExistingNumberOfShares) {
    SecurityInfo security = existingTrade.getSecurity();
    double preExistingAveragePrice = (security.getAveragePrice() * security.getNumberOfShares())
        - (existingTrade.getPrice() * existingTrade.getNumberOfShares());
    if (preExistingNumberOfShares == 0) {
      return MINIMUM_BUY_PRICE;
    }
    return preExistingAveragePrice / preExistingNumberOfShares;
  }

  public static SecurityInfo getUpdatedSecurityInfo(SecurityInfo security, Trade mergedTrade) {
    if(mergedTrade.getTradeType().equals("BUY")) {
      security.setAveragePrice(calculateAveragePrice(
          security, mergedTrade.getNumberOfShares(), mergedTrade.getPrice()));
      security.setNumberOfShares(getTotalNumberOfShares(security.getNumberOfShares(), mergedTrade.getNumberOfShares()));
    } else if(mergedTrade.getTradeType().equals("SELL")) {
      int numberOfShares = security.getNumberOfShares() - mergedTrade.getNumberOfShares();
      if (numberOfShares < 0)
        throw new SecuritySellException("Requested number of shares to sell are more than the actual number of shares");
      security.setNumberOfShares(numberOfShares);
    }
    return security;
  }

  private static Double calculateAveragePrice(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return getTotalPrice(security, numberOfShares, pricePerShare) / getTotalNumberOfShares(security.getNumberOfShares(),
        numberOfShares);
  }

  private static int getTotalNumberOfShares(int existingNumberOfShares, int newShares) {
    return existingNumberOfShares + newShares;
  }

  private static Double getTotalPrice(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return (security.getAveragePrice() * security.getNumberOfShares())
        + (numberOfShares * pricePerShare);
  }
}
