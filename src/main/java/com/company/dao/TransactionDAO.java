package com.company.dao;

import com.company.entities.Account;
import com.company.entities.Transaction;

import javax.persistence.EntityManager;
import java.util.Date;

public class TransactionDAO {

    public void addTransaction(EntityManager em, Double sum, Account account) {
        Transaction transaction = new Transaction();
        transaction.setSum(sum);
        transaction.setDate(new Date());
        transaction.setAccount(account);

        em.getTransaction().begin();
        try{
            em.merge(transaction);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }
}
