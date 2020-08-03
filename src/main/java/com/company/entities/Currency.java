package com.company.entities;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String currencyCode;
    private Double sum;
    private String numberAcc;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "acc_cur",
               joinColumns = {@JoinColumn(name = "cur_id", referencedColumnName = "id")},
               inverseJoinColumns = {@JoinColumn(name = "acc_id", referencedColumnName = "id")})
    Set<Account> accounts = new HashSet<>();

    public Currency() {
    }

    public Currency(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumberAcc() {
        return numberAcc;
    }

    public void setNumberAcc(String numberAcc) {
        this.numberAcc = numberAcc;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.currencies.add(this);
    }

    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }
}
