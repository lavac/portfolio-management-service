package com.smallcase.portfolio.service;

import com.smallcase.portfolio.controller.dto.AggregatedPortfolioInformation;
import com.smallcase.portfolio.exception.NoPortfolioFoundException;
import com.smallcase.portfolio.repository.PortfolioRepository;
import com.smallcase.portfolio.repository.SecurityRepository;
import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {

  @Autowired
  PortfolioRepository portfolioRepository;

  @Autowired
  SecurityRepository securityRepository;

  public static final Double CURRENT_PRICE = 100.00;

  public List<AggregatedPortfolioInformation> fetchPortfolio(Integer portfolioId) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoPortfolioFoundException("Portfolio Id not found exception"));

    List<SecurityInfo> securities = securityRepository.findByPortfolio(portfolio);

    ArrayList<AggregatedPortfolioInformation> securityInfos = new ArrayList<>();
    for(SecurityInfo securityInfo: securities) {
      AggregatedPortfolioInformation aggregatedPortfolioInformation = AggregatedPortfolioInformation
          .builder()
          .averageBuyPrice(securityInfo.getAveragePrice())
          .shares(securityInfo.getNumberOfShares())
          .tickerSymbol(securityInfo.getTickerSymbol())
          .build();
      securityInfos.add(aggregatedPortfolioInformation);
    }
    return securityInfos;
  }

  public Double fetchReturns(Integer portfolioId) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new NoPortfolioFoundException("Portfolio Id not found exception"));

    List<SecurityInfo> securities = securityRepository.findByPortfolio(portfolio);

    Double returns = 0.0;
    for(SecurityInfo securityInfo: securities) {
      returns += ((CURRENT_PRICE - securityInfo.getAveragePrice()) * securityInfo.getNumberOfShares());
    }
    return returns;
  }
}
