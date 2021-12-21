package com.smallcase.portfolio.service;

import com.smallcase.portfolio.exception.PortfolioNotFoundException;
import com.smallcase.portfolio.repository.PortfolioRepository;
import com.smallcase.portfolio.repository.SecurityRepository;
import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

  @Autowired
  PortfolioRepository portfolioRepository;

  @Autowired
  SecurityRepository securityRepository;

  public List<SecurityInfo> fetchPortfolio(Integer portfolioId) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new PortfolioNotFoundException("Portfolio Id not found exception"));

    return securityRepository.findByPortfolio(portfolio);
  }

  public Double fetchReturns(Integer portfolioId) {
    Portfolio portfolio = portfolioRepository.findById(portfolioId)
        .orElseThrow(() -> new PortfolioNotFoundException("Portfolio Id not found exception"));

    // Calculate Returns
    return 0.0;
  }
}
