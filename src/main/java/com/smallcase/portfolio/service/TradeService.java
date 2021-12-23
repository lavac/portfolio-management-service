package com.smallcase.portfolio.service;

import com.smallcase.portfolio.controller.dto.AggregatedSecurityInformation;
import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.controller.dto.TradeUpdateRequest;
import com.smallcase.portfolio.exception.*;
import com.smallcase.portfolio.model.TradeModel;
import com.smallcase.portfolio.repository.TradeRepository;
import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import com.smallcase.portfolio.repository.entity.Trade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.smallcase.portfolio.service.SecurityUtils.getUpdatedSecurityInfo;
import static com.smallcase.portfolio.service.SecurityUtils.recoverPreExistingSecurity;

@Service
public class TradeService {

  @Autowired
  TradeRepository tradeRepository;

  @Autowired
  SecurityService securityService;

  @Autowired
  PortfolioService portfolioService;

  ModelMapper modelMapper = new ModelMapper();

  public TradeModel add(Integer portFolioId, String tickerSymbol, TradeCreateRequest tradeCreateRequest) {
    Portfolio portfolio = portfolioService.findById(portFolioId);

      SecurityInfo updatedSecurityInfo = securityService
          .getUpdatedSecurityInfo(tradeCreateRequest, tickerSymbol, portfolio);

      Trade trade = Trade.builder()
          .tradeType(tradeCreateRequest.getTradeType().toString())
          .numberOfShares(tradeCreateRequest.getNumberOfShares())
          .price(tradeCreateRequest.getPricePerShare().doubleValue())
          .portfolioId(portFolioId)
          .security(updatedSecurityInfo)
          .build();

    Trade savedTrade = tradeRepository.save(trade);
    return modelMapper.map(savedTrade, TradeModel.class);
  }

  public List<AggregatedSecurityInformation> findAllByPortfolioId(Integer portFolioId) {
    HashMap<String, List<TradeModel>> securityTradesMap = new HashMap<>();

    tradeRepository.findByPortfolioId(portFolioId)
        .forEach((Trade trade) -> {
          TradeModel tradeModel = modelMapper.map(trade, TradeModel.class);
          SecurityInfo security = trade.getSecurity();
          String tickerSymbol = security.getTickerSymbol();
          if (securityTradesMap.containsKey(tickerSymbol)) {
            List<TradeModel> trades = securityTradesMap.get(tickerSymbol);
            trades.add(tradeModel);
            securityTradesMap.put(tickerSymbol, trades);
          } else {
            ArrayList<TradeModel> trades = new ArrayList<>();
            trades.add(tradeModel);
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

  public TradeModel update(Integer updateId, TradeUpdateRequest tradeUpdateRequest) {
    Trade existingTrade = tradeRepository.findById(updateId)
        .orElseThrow(() -> new TradeIdNotFoundException("Trade Id not found exception"));

    Trade mergedTrade = new Trade();
    mergedTrade.setId(existingTrade.getId());
    mergedTrade.setTradeType(existingTrade.getTradeType());
    mergedTrade.setNumberOfShares(existingTrade.getNumberOfShares());
    mergedTrade.setPrice(existingTrade.getPrice());
    mergedTrade.setPortfolioId(existingTrade.getPortfolioId());

    SecurityInfo securityInfo = recoverPreExistingSecurity(existingTrade);

    if(tradeUpdateRequest.getTradeType() != null) {
        mergedTrade.setTradeType(tradeUpdateRequest.getTradeType().toString());
      } if (tradeUpdateRequest.getNumberOfShares() != null) {
        mergedTrade.setNumberOfShares(tradeUpdateRequest.getNumberOfShares());
    } if (tradeUpdateRequest.getPricePerShare() != null) {
        mergedTrade.setPrice(tradeUpdateRequest.getPricePerShare());
    }
    SecurityInfo updatedSecurityInfo = getUpdatedSecurityInfo(securityInfo, mergedTrade);
    mergedTrade.setSecurity(updatedSecurityInfo);

    Trade savedTrade = tradeRepository.save(mergedTrade);
    return modelMapper.map(savedTrade, TradeModel.class);
  }

  public void removeTradeById(Integer tradeId) {
    Trade trade = tradeRepository.findById(tradeId)
        .orElseThrow(() -> new TradeIdNotFoundException("Trade Id not found exception"));
    SecurityInfo securityInfo = recoverPreExistingSecurity(trade);
    securityService.save(securityInfo);
    tradeRepository.deleteById(tradeId);
  }
}
