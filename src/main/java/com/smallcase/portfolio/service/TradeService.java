package com.smallcase.portfolio.service;

import com.smallcase.portfolio.controller.dto.AggregatedSecurityInformation;
import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.controller.dto.TradeUpdateRequest;
import com.smallcase.portfolio.exception.*;
import com.smallcase.portfolio.model.TradeType;
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

  public Trade add(Integer portFolioId, String tickerSymbol, TradeCreateRequest tradeCreateRequest) {
    Portfolio portfolio = portfolioRepository.findById(portFolioId)
        .orElseThrow(() -> new NoPortfolioFoundException("No Portfolio found exception"));

      SecurityInfo existingSecurity = securityRepository
          .findByTickerSymbolAndPortfolio(tickerSymbol, portfolio)
          .orElse(null);

      SecurityInfo updatedSecurityInfo = (existingSecurity != null)
      ? getUpdatedSecurityInfo(existingSecurity, tradeCreateRequest)
          : getNewSecurityInfo(tickerSymbol, tradeCreateRequest, portfolio);
      securityRepository.save(updatedSecurityInfo);

      Trade trade = Trade.builder()
          .tradeType(tradeCreateRequest.getTradeType().toString())
          .numberOfShares(tradeCreateRequest.getNumberOfShares())
          .price(tradeCreateRequest.getPricePerShare().doubleValue())
          .portfolioId(portFolioId)
          .security(updatedSecurityInfo)
          .build();

      return tradeRepository.save(trade);
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

  private SecurityInfo getUpdatedSecurityInfo(SecurityInfo security, TradeCreateRequest tradeCreateRequest) {
    if(tradeCreateRequest.getTradeType().equals(TradeType.BUY)) {
      security.setAveragePrice(
          calculateAveragePrice(security, tradeCreateRequest.getNumberOfShares(),
              tradeCreateRequest.getPricePerShare().doubleValue()));
      security.setNumberOfShares(security.getNumberOfShares() + tradeCreateRequest.getNumberOfShares());
    } else if(tradeCreateRequest.getTradeType().equals(TradeType.SELL)) {
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

  public List<AggregatedSecurityInformation> findAllByPortfolioId(Integer portFolioId) {
    HashMap<String, List<Trade>> securityTradesMap = new HashMap<>();

    tradeRepository.findByPortfolioId(portFolioId)
        .forEach((Trade trade) -> {
          SecurityInfo security = trade.getSecurity();
          String tickerSymbol = security.getTickerSymbol();
          if (securityTradesMap.containsKey(tickerSymbol)) {
            List<Trade> trades = securityTradesMap.get(tickerSymbol);
            trades.add(trade);
            securityTradesMap.put(tickerSymbol, trades);
          } else {
            ArrayList<Trade> trades = new ArrayList<>();
            trades.add(trade);
            securityTradesMap.put(tickerSymbol, trades);
          }
        });
    ArrayList<AggregatedSecurityInformation> aggregatedSecurities = new ArrayList<>();
    securityTradesMap.forEach((tickerSymbol, trades) -> {
      AggregatedSecurityInformation aggregatedSecurityInformation = AggregatedSecurityInformation
          .builder()
          .tickerSymbol(tickerSymbol)
          .trades(trades)
          .build();
      aggregatedSecurities.add(aggregatedSecurityInformation);
    });
    return aggregatedSecurities;
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
        mergedTrade.setTradeType(tradeUpdateRequest.getTradeType().toString());
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
      if (preExistingAveragePrice <= 0.0 || preExistingNumberOfShares < 0) {
        throw new InvalidTradeOperation("Cannot update or remove this trade");
      }
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
