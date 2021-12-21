package com.smallcase.portfolio.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "portfolio")
public class Portfolio {
    @Id
    private Integer id;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "account_number")
    private CustomerInformation accountNumber;
}
