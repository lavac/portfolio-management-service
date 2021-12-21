package com.smallcase.portfolio.repository.entity;


import lombok.*;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "security_info")
public class SecurityInfo {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "ticker_symbol")
    private String tickerSymbol;
    @Column(name = "number_of_shares")
    private Integer numberOfShares;
    @Column(name = "average_price")
    private Double averagePrice;
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "portFolio_id")
    private Portfolio portfolio;
}
