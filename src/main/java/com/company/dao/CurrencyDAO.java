package com.company.dao;

import com.company.entities.Account;
import com.company.entities.Currency;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class CurrencyDAO {

    public void addCurrency(EntityManager em, Double sum, String cur, Account account) {
        Currency currency = null;
        String numberAcc = account.getNumber();

        if(getCurrencyObjById(em, numberAcc, cur) != null) {
            currency = getCurrencyObjById(em, numberAcc, cur);
            Double sumOld = currency.getSum();
            String curOld = currency.getCurrencyCode();
            if(curOld.equals(cur)) {

                sum += sumOld;
            } else {
                currency = new Currency();
            }
        } else {
            currency = new Currency();
        }
        currency.setSum(sum);
        currency.setCurrencyCode(cur);
        currency.addAccount(account);
        currency.setNumberAcc(numberAcc);

        em.getTransaction().begin();
        try{
            em.persist(currency);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public Currency getCurrencyObjById(EntityManager em, String numberAcc, String cur) {
        Currency currency = null;
        try{
            Query query = em.createQuery("SELECT c FROM Currency c WHERE c.numberAcc = :numberAcc AND c.currencyCode = :cur");
            query.setParameter("numberAcc", numberAcc);
            query.setParameter("cur", cur);

            currency = (Currency) query.getSingleResult();

        } catch (NoResultException e) {
            System.out.println("No currencies found");
        }
        return currency;
    }

}
