package com.smallcase.portfolio.service;

import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.controller.dto.TradeUpdateRequest;
import com.smallcase.portfolio.exception.*;
import com.smallcase.portfolio.repository.PortfolioRepository;
import com.smallcase.portfolio.repository.SecurityRepository;
import com.smallcase.portfolio.repository.TradeRepository;
import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import com.smallcase.portfolio.repository.entity.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradeService {

  @Autowired
  TradeRepository tradeRepository;

  @Autowired
  SecurityRepository securityRepository;

  @Autowired
  PortfolioRepository portfolioRepository;

  public Trade add(Integer portFolioId, Integer securityId, TradeCreateRequest tradeCreateRequest) {
    Portfolio portfolio = portfolioRepository.findById(portFolioId)
        .orElseThrow(() -> new NoPortfolioFoundException("No Portfolio found exception"));

    SecurityInfo securityInfo;

    if (securityId != null) {
      SecurityInfo existingSecurity = securityRepository
          .findByIdAndTickerSymbol(securityId, tradeCreateRequest.getTickerSymbol())
          .orElseThrow(() -> new NoSecurityFoundException("No Security found exception"));
      securityInfo = getUpdatedSecurityInfo(existingSecurity, tradeCreateRequest);
    }
    else {
      SecurityInfo existingSecurity = securityRepository
          .findByTickerSymbolAndPortfolio(tradeCreateRequest.getTickerSymbol(), portfolio)
          .orElse(null);

      SecurityInfo updatedSecurityInfo = (existingSecurity != null)
      ? getUpdatedSecurityInfo(existingSecurity, tradeCreateRequest)
          : getNewSecurityInfo(tradeCreateRequest, portfolio);
      securityInfo = securityRepository.save(updatedSecurityInfo);
    }

      Trade trade = Trade.builder()
          .tradeType(tradeCreateRequest.getTradeType())
          .numberOfShares(tradeCreateRequest.getNumberOfShares())
          .price(tradeCreateRequest.getPricePerShare())
          .security(securityInfo)
          .build();

      return tradeRepository.save(trade);
  }

  private SecurityInfo getNewSecurityInfo(TradeCreateRequest tradeCreateRequest, Portfolio portfolio) {
    return SecurityInfo
        .builder()
        .tickerSymbol(tradeCreateRequest.getTickerSymbol())
        .portfolio(portfolio)
        .averagePrice(tradeCreateRequest.getPricePerShare())
        .numberOfShares(tradeCreateRequest.getNumberOfShares())
        .build();
  }

  private SecurityInfo getUpdatedSecurityInfo(SecurityInfo security, TradeCreateRequest tradeCreateRequest) {
    if(tradeCreateRequest.getTradeType().equals("BUY")) {
      security.setAveragePrice(
          calculateAveragePrice(security, tradeCreateRequest.getNumberOfShares(), tradeCreateRequest.getPricePerShare()));
      security.setNumberOfShares(security.getNumberOfShares() + tradeCreateRequest.getNumberOfShares());
    } else if(tradeCreateRequest.getTradeType().equals("SELL")) {
      int numberOfShares = security.getNumberOfShares() - tradeCreateRequest.getNumberOfShares();
      if (numberOfShares < 0)
        throw new SecuritySellException("Requested number of shares to sell are more than the actual number of shares");
      security.setNumberOfShares(numberOfShares);
    }
    return security;
  }

  private SecurityInfo getUpdatedSecurityInfo(SecurityInfo security, Trade mergedTrade) {
    if(mergedTrade.getTradeType().equals("BUY")) {
      security.setAveragePrice(calculateAveragePrice(
          security, mergedTrade.getNumberOfShares(), mergedTrade.getPrice()));
      security.setNumberOfShares(security.getNumberOfShares() + mergedTrade.getNumberOfShares());
    } else if(mergedTrade.getTradeType().equals("SELL")) {
      int numberOfShares = security.getNumberOfShares() - mergedTrade.getNumberOfShares();
      if (numberOfShares < 0)
        throw new SecuritySellException("Requested number of shares to sell are more than the actual number of shares");
      security.setNumberOfShares(numberOfShares);
    }
    return security;
  }

  private Double calculateAveragePrice(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return getaDouble(security, numberOfShares, pricePerShare) / (security.getNumberOfShares() + numberOfShares);
  }

  private double getaDouble(SecurityInfo security, Integer numberOfShares, Double pricePerShare) {
    return (security.getAveragePrice() * security.getNumberOfShares())
        + (numberOfShares * pricePerShare);
  }

  public HashMap<Integer, List<Trade>> findAll() {
    HashMap<Integer, List<Trade>> securityTradesMap = new HashMap<>();

    tradeRepository.findAll()
        .forEach((Trade trade) -> {
          SecurityInfo security = trade.getSecurity();
          Integer id = security.getId();
          if (securityTradesMap.containsKey(id)) {
            List<Trade> trades = securityTradesMap.get(id);
            trades.add(trade);
            securityTradesMap.put(id, trades);
          } else {
            ArrayList<Trade> trades = new ArrayList<>();
            trades.add(trade);
            securityTradesMap.put(id, trades);
          }
        });
    // TODO form object
    return securityTradesMap;
  }
  public Trade update(Integer updateId, TradeUpdateRequest tradeUpdateRequest) {
    Trade existingTrade = tradeRepository.findById(updateId)
        .orElseThrow(() -> new TradeIdNotFoundException("Trade Id not found exception"));

    Trade mergedTrade = new Trade();
    mergedTrade.setId(existingTrade.getId());
    mergedTrade.setTradeType(existingTrade.getTradeType());
    mergedTrade.setNumberOfShares(existingTrade.getNumberOfShares());
    mergedTrade.setPrice(existingTrade.getPrice());

    SecurityInfo securityInfo = recoverPreExistingSecurity(existingTrade);

    if(tradeUpdateRequest.getTradeType() != null) {
        mergedTrade.setTradeType(tradeUpdateRequest.getTradeType());
      } if (tradeUpdateRequest.getNumberOfShares() != null) {
        mergedTrade.setNumberOfShares(tradeUpdateRequest.getNumberOfShares());
    } if (tradeUpdateRequest.getPricePerShare() != null) {
        mergedTrade.setPrice(tradeUpdateRequest.getPricePerShare());
    }
      // TODO Handle exception for update part alone
    SecurityInfo updatedSecurityInfo = getUpdatedSecurityInfo(securityInfo, mergedTrade);
    mergedTrade.setSecurity(updatedSecurityInfo);

    return tradeRepository.save(mergedTrade);
  }

  public void removeTradeById(Integer tradeId) {
    Trade trade = tradeRepository.findById(tradeId)
        .orElseThrow(() -> new TradeIdNotFoundException("Trade Id not found exception"));
    SecurityInfo securityInfo = recoverPreExistingSecurity(trade);
    securityRepository.save(securityInfo);
    tradeRepository.deleteById(tradeId);
  }

  private SecurityInfo recoverPreExistingSecurity(Trade existingTrade) {
    SecurityInfo security = existingTrade.getSecurity();
    if (existingTrade.getTradeType().equals("BUY")) {
      int preExistingNumberOfShares = getNumberOfSharesBeforeThisTrade(security.getNumberOfShares(),
          existingTrade.getNumberOfShares(), existingTrade.getTradeType());
      Double preExistingAveragePrice = getPreExistingAveragePriceForBuyType(existingTrade,
          preExistingNumberOfShares);
      security.setNumberOfShares(preExistingNumberOfShares);
      security.setAveragePrice(preExistingAveragePrice);
    } else {
      int preExistingNumberOfShares = getNumberOfSharesBeforeThisTrade(security.getNumberOfShares(),
          existingTrade.getNumberOfShares(), existingTrade.getTradeType());
      security.setNumberOfShares(preExistingNumberOfShares);
    }
    return security;
  }

  private int getNumberOfSharesBeforeThisTrade(Integer totalNumberOfShares, Integer numberOfSharesInThisTrade,
                                               String tradeType) {
    return  (tradeType.equals("BUY"))
        ? (totalNumberOfShares - numberOfSharesInThisTrade)
        : totalNumberOfShares + numberOfSharesInThisTrade;
  }

  private double getPreExistingAveragePriceForBuyType(Trade existingTrade, int preExistingNumberOfShares) {
    SecurityInfo security = existingTrade.getSecurity();
    double preExistingAveragePrice = (security.getAveragePrice() * security.getNumberOfShares())
        - (existingTrade.getPrice() * existingTrade.getNumberOfShares());
    double average = preExistingAveragePrice / preExistingNumberOfShares;
    return average;
  }
}
