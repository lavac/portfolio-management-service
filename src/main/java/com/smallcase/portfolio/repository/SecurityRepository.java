package com.smallcase.portfolio.repository;

import com.smallcase.portfolio.repository.entity.Portfolio;
import com.smallcase.portfolio.repository.entity.SecurityInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityRepository extends CrudRepository<SecurityInfo, Integer> {

  Optional<SecurityInfo> findByIdAndTickerSymbol(Integer securityId, String portfolio);

  Optional<SecurityInfo> findByTickerSymbolAndPortfolio(String tickerSymbol, Portfolio portfolio);

  List<SecurityInfo> findByPortfolio(Portfolio portfolio);
}
