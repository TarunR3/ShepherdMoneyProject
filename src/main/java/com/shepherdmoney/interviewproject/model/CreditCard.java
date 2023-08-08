package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String number;

    //Credit card where each multiple instances of a CreditCard object can be associated with a single user.
    // Join column specifies user_id as containing the foreign key.
    // TODO: Credit card's owner. For detailed hint, please see User class
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //BalanceHistory as an OneToMany relationship since a credit card can have multiple BalanceHistory objects associated with it.
    //Ordered by date DESC to have the most recent date first in the list.
    @OneToMany(cascade = CascadeType.ALL)
    @OrderBy("date DESC")
    private List<BalanceHistory> balanceHistory = new ArrayList<>();
    // TODO: Credit card's balance history. It is a requirement that the dates in the balanceHistory 
    //       list must be in chronological order, with the most recent date appearing first in the list. 
    //       Additionally, the first object in the list must have a date value that matches today's date, 
    //       since it represents the current balance of the credit card. For example:
    //       [
    //         {date: '2023-04-13', balance: 1500},
    //         {date: '2023-04-12', balance: 1200},
    //         {date: '2023-04-11', balance: 1000},
    //         {date: '2023-04-10', balance: 800}
    //       ]
}
