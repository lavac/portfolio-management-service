package com.smallcase.portfolio.repository.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer_info")
public class CustomerInformation {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String accountNumber;
    private String name;
}
