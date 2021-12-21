package com.smallcase.portfolio.repository;

import com.smallcase.portfolio.repository.entity.Portfolio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository  extends CrudRepository<Portfolio, Integer> {
}
