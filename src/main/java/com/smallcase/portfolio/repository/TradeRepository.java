package com.smallcase.portfolio.repository;

import com.smallcase.portfolio.repository.entity.Trade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends CrudRepository<Trade, Integer> {
  List<Trade> findByPortfolioId(Integer portfolioId);
}


