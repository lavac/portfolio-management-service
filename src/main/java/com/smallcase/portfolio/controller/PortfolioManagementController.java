package com.smallcase.portfolio.controller;

import com.smallcase.portfolio.controller.dto.AggregatedPortfolioInformation;
import com.smallcase.portfolio.controller.dto.AggregatedSecurityInformation;
import com.smallcase.portfolio.controller.dto.TradeCreateRequest;
import com.smallcase.portfolio.controller.dto.TradeUpdateRequest;
import com.smallcase.portfolio.exception.TradeUpdateException;
import com.smallcase.portfolio.service.PortfolioService;
import com.smallcase.portfolio.service.TradeService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.smallcase.portfolio.repository.entity.Trade;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/portfolio")
public class PortfolioManagementController {

  @Autowired
  TradeService tradeService;

  @Autowired
  PortfolioService portfolioService;

  @PostMapping("/{portFolioId}/securities/{tickerSymbol}/trades")
  public ResponseEntity<Trade> addTrade(@PathVariable @Valid @NonNull Integer portFolioId,
                                        @PathVariable @Valid @NonNull String tickerSymbol,
                                        @RequestBody @Valid TradeCreateRequest tradeCreateRequest) {
    return ResponseEntity.accepted().body(tradeService.add(portFolioId, tickerSymbol, tradeCreateRequest));
  }

  @GetMapping("/{portFolioId}/aggregated-securities")
  public ResponseEntity<List<AggregatedSecurityInformation>> fetchTrades(@PathVariable @Valid @NonNull Integer portFolioId) {
    List<AggregatedSecurityInformation> aggregatedSecurityInformation = tradeService.findAllByPortfolioId(portFolioId);
    return ResponseEntity.accepted().body(aggregatedSecurityInformation);
  }

  @PutMapping("/{portFolioId}/trades/{tradeId}")
  public ResponseEntity<Trade> updateTrade(@PathVariable  @Valid @NonNull Integer tradeId,
                                           @RequestBody TradeUpdateRequest tradeUpdateRequest) {
    validateTradeUpdateRequest(tradeUpdateRequest);
    return ResponseEntity.accepted().body(tradeService.update(tradeId, tradeUpdateRequest));
  }

  private void validateTradeUpdateRequest(TradeUpdateRequest tradeUpdateRequest) {
    if (tradeUpdateRequest.getPricePerShare() != null && tradeUpdateRequest.getPricePerShare() < 1.0) {
        throw new TradeUpdateException("pricePerShare should not be less than 1.0");
    }
    if (tradeUpdateRequest.getNumberOfShares() != null && tradeUpdateRequest.getNumberOfShares() <= 0) {
      throw new TradeUpdateException("numberOfShares should not be less than 1");
    }
  }

  @DeleteMapping("/{portFolioId}/trades/{tradeId}")
  public void removeTrade(@PathVariable @Valid @NonNull Integer tradeId) {
    tradeService.removeTradeById(tradeId);
  }

  @GetMapping("/{portfolioId}")
  public ResponseEntity<List<AggregatedPortfolioInformation>> fetchPortfolio(@PathVariable @Valid @NonNull Integer portfolioId) {
    List<AggregatedPortfolioInformation> aggregatedPortfolioInformation = portfolioService.fetchPortfolioSummary(portfolioId);
    return ResponseEntity.accepted().body(aggregatedPortfolioInformation);
  }

  @GetMapping("/{portfolioId}/returns")
  public ResponseEntity<Double> fetchReturns(@PathVariable @Valid @NonNull Integer portfolioId) {
    Double returns = portfolioService.fetchReturns(portfolioId);
    return ResponseEntity.accepted().body(returns);
  }
}
