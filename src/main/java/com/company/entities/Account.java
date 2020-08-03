package com.company.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String number;

    private Double sum;
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private final List<Transaction> transactions = new ArrayList<>();

    @ManyToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    Set<Currency> currencies = new HashSet<>();

    public Account() {
    }

    public Account(String number, Double sum, String currency) {
        this.number = number;
        this.sum = sum;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addAccounts(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }

    public void addCurrency(Currency currency) {
        currencies.add(currency);
        currency.accounts.add(this);
    }

    public Set<Currency> getCurrencies () {
        return Collections.unmodifiableSet(currencies);
    }
}
