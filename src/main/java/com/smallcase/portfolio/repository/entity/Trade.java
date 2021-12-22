package com.smallcase.portfolio.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "trade")
public class Trade {
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "trade_type")
  private String tradeType;

  @Column(name = "price")
  private Double price;

  @Column(name = "number_of_shares")
  private Integer numberOfShares;

  @Column(name = "portfolio_id")
  private Integer portfolioId;

  @ManyToOne(cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "security_id")
  private SecurityInfo security;
}
