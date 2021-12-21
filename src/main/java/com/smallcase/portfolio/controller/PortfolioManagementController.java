package com.smallcase.portfolio.controller;

import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.controller.dto.TradeUpdateRequest;
import com.smallcase.portfolio.repository.PortfolioRepository;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import com.smallcase.portfolio.service.PortfolioService;
import com.smallcase.portfolio.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smallcase.portfolio.repository.entity.Trade;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioManagementController {

  @Autowired
  PortfolioRepository portfolioRepository;

  @Autowired
  TradeService tradeService;

  @Autowired
  PortfolioService portfolioService;

  @PostMapping("/{portFolioId}/securities/{securityId}/trades")
  public ResponseEntity<Trade> addTrade(@PathVariable Integer portFolioId,
                                        @PathVariable Integer securityId,
                                        @RequestBody TradeCreateRequest tradeCreateRequest) {
    return ResponseEntity.accepted().body(tradeService.add(portFolioId, securityId, tradeCreateRequest));
  }

  @GetMapping("/{portFolioId}/securities/{securityId}/trades")
  public HashMap<Integer, List<Trade>> fetchTrades() {
    return tradeService.findAll();
  }

  @PutMapping("/{portFolioId}/securities/{securityId}/trades/{tradeId}")
  public ResponseEntity<Trade> updateTrade(@PathVariable Integer tradeId,
                                           @RequestBody TradeUpdateRequest tradeUpdateRequest) {
    return ResponseEntity.accepted().body(tradeService.update(tradeId, tradeUpdateRequest));
  }

  @DeleteMapping("/{portFolioId}/securities/{securityId}/trades/{tradeId}")
  public void removeTrade(@PathVariable Integer tradeId) {
    tradeService.removeTradeById(tradeId);
  }

  @GetMapping("/{portfolioId}")
  public ResponseEntity<List<SecurityInfo>> fetchPortfolio(@PathVariable Integer portfolioId) {
    List<SecurityInfo> securityInfos = portfolioService.fetchPortfolio(portfolioId);
    return ResponseEntity.accepted().body(securityInfos);
  }

  @GetMapping("/{portfolioId}/returns")
  public ResponseEntity<Double> fetchReturns(@PathVariable Integer portfolioId) {
    Double returns = portfolioService.fetchReturns(portfolioId);
    return ResponseEntity.accepted().body(returns);
  }
}
