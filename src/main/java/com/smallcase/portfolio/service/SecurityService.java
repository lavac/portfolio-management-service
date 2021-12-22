package com.smallcase.portfolio.service;

import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.exception.SecuritySellException;
import com.smallcase.portfolio.model.TradeType;
import com.smallcase.portfolio.repository.SecurityRepository;
import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  @Autowired
  SecurityRepository securityRepository;


  public SecurityInfo getUpdatedSecurityInfo(TradeCreateRequest tradeCreateRequest, String tickerSymbol, Portfolio portfolio) {
    SecurityInfo existingSecurity = securityRepository
        .findByTickerSymbolAndPortfolio(tickerSymbol, portfolio)
        .orElse(null);

    SecurityInfo updatedSecurityInfo = (existingSecurity != null)
        ? getUpdatedSecurityInfo(existingSecurity, tradeCreateRequest)
        : getNewSecurityInfo(tickerSymbol, tradeCreateRequest, portfolio);
    securityRepository.save(updatedSecurityInfo);
    return updatedSecurityInfo;
  }

  public void save(SecurityInfo securityInfo) {
    securityRepository.save(securityInfo);
  }

  private SecurityInfo getUpdatedSecurityInfo(SecurityInfo security, TradeCreateRequest tradeCreateRequest) {
    if(tradeCreateRequest.getTradeType().equals(TradeType.BUY)) {
      security.setAveragePrice(
          calculateAveragePrice(security, tradeCreateRequest.getNumberOfShares(),
              tradeCreateRequest.getPricePerShare().doubleValue()));
      security.setNumberOfShares(getTotalNumberOfShares(security.getNumberOfShares(), tradeCreateRequest.getNumberOfShares()));
    } else if(tradeCreateRequest.getTradeType().equals(TradeType.SELL)) {
      int numberOfShares = security.getNumberOfShares() - tradeCreateRequest.getNumberOfShares();
      if (numberOfShares < 0)
        throw new SecuritySellException("Requested number of shares to sell are more than the actual number of shares");
      security.setNumberOfShares(numberOfShares);
    }
    return security;
  }

  private Double calculateAveragePrice(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return getTotalPrice(security, numberOfShares, pricePerShare) / getTotalNumberOfShares(security.getNumberOfShares(), numberOfShares);
  }

  private int getTotalNumberOfShares(int existingNumberOfShares, int newShares) {
    return existingNumberOfShares + newShares;
  }

  private Double getTotalPrice(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return (security.getAveragePrice() * security.getNumberOfShares())
        + (numberOfShares * pricePerShare);
  }

  private SecurityInfo getNewSecurityInfo(String tickerSymbol, TradeCreateRequest tradeCreateRequest, Portfolio portfolio) {
    return SecurityInfo
        .builder()
        .tickerSymbol(tickerSymbol)
        .portfolio(portfolio)
        .averagePrice(tradeCreateRequest.getPricePerShare().doubleValue())
        .numberOfShares(tradeCreateRequest.getNumberOfShares())
        .build();
  }
}
