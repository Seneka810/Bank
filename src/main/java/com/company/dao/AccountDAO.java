package com.company.dao;

import com.company.entities.Account;
import org.apache.commons.math3.util.Precision;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class AccountDAO {
    private final CustomerDAO customer = new CustomerDAO();
    private final TransactionDAO transaction = new TransactionDAO();
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public void selectAccountsByCustomers(EntityManager em, String name) {

        int idCustomer = customer.selectCustomerByName(em, name);
        try{
            Query query = em.createQuery("SELECT a FROM Account a WHERE a.customer.id = :id");
            query.setParameter("id", idCustomer);
            view(query);
        } catch (NoResultException e) {
            System.out.println("No accounts found");
        }
    }

    public double getTotalAmount(EntityManager em, String name) {

        double totalAmount = 0.0;
        String cur;

        int idCustomer = customer.selectCustomerByName(em, name);
        try{
            Query query = em.createQuery("SELECT a FROM Account a WHERE a.customer.id = :id");
            query.setParameter("id", idCustomer);
            List<Account> accounts = query.getResultList();
            for (int i = 0; i < accounts.size(); i++) {
                cur = accounts.get(i).getCurrency();
                if("EUR".equals(cur)) {
                    totalAmount += accounts.get(i).getSum()*33;
                } else if("USD".equals(cur)) {
                    totalAmount += accounts.get(i).getSum()*27;
                } else {
                    totalAmount += accounts.get(i).getSum();
                }
            }
        } catch (NoResultException e) {
            System.out.println("No accounts found");
        }

        return Precision.round(totalAmount, 2);

    }

    public void transferSum(EntityManager em, String name, int from, int to, double sum) {
        double transferSum;
        String cur = null;
        int idCustomer = customer.selectCustomerByName(em, name);
        try{
            Query query = em.createQuery("SELECT a FROM Account a WHERE a.customer.id = :id");
            query.setParameter("id", idCustomer);
            List<Account> accounts = query.getResultList();
            for (int i = 0; i < accounts.size(); i++) {
                if(accounts.get(i).getId() == from) {
                    transferSum = accounts.get(i).getSum() - sum;
                    if(transferSum < 0) {
                        throw new Exception();
                    }
                    cur = accounts.get(i).getCurrency();
                    accounts.get(i).setSum(transferSum);
                }
            }

            for (int i = 0; i < accounts.size(); i++) {
                if(accounts.get(i).getId() == to) {
                    if (!cur.equals(accounts.get(i).getCurrency())) {
                        String curAcc = accounts.get(i).getCurrency();
                        if("UAH".equals(cur) && "EUR".equals(curAcc)){
                            sum /= 33;
                        } else if("UAH".equals(cur) && "USD".equals(curAcc)){
                            sum /= 27;
                        } else if("EUR".equals(cur) && "USD".equals(curAcc)){
                            sum *= 1.5;
                        } else if("EUR".equals(cur) && "UAH".equals(curAcc)){
                            sum *= 33;
                        } else if("USD".equals(cur) && "EUR".equals(curAcc)){
                            sum /= 1.5;
                        } else if("USD".equals(cur) && "UAH".equals(curAcc)){
                            sum *= 27;
                        }
                    }
                    transferSum = accounts.get(i).getSum() + sum;
                    accounts.get(i).setSum(Precision.round(transferSum, 2));
                }
            }

            System.out.println("id\t" + "number\t" + "sum\t" + "currency");
            for (Account account : accounts) {
                System.out.println(account.getId() + "\t"
                        + account.getNumber() + "\t"
                        + account.getSum() + "\t"
                        + account.getCurrency());
                System.out.println();
            }
        } catch (NoResultException e) {
            System.out.println("No accounts found");
        } catch (Exception e) {
            System.out.println("You do not have enough money");;
        }
    }

    public void topUpAccount(EntityManager em, int id, double sum, String name, String cur) {

        try{
            Query query = em.createQuery("SELECT a FROM Account a WHERE a.id = :id");
            query.setParameter("id", id);
            Account account = (Account) query.getSingleResult();
            Double sumAcc = account.getSum();
            String currency = account.getCurrency();
            sumAcc += sum;
            account.setCurrency(cur);

            convertAccToCur(cur, account, sumAcc, currency);

            em.merge(account);
            transaction.addTransaction(em, sum, account);
            currencyDAO.addCurrency(em, sum, cur, account);
            selectAccountsByCustomers(em, name);
        } catch (NoResultException e) {
            System.out.println("No accounts found");
        }

    }

    private void convertAccToCur(String cur, Account account, Double sumAcc, String currency) {
        if(!cur.equals(currency)) {
            if("UAH".equals(cur) && "EUR".equals(currency)){
                account.setSum(Precision.round(sumAcc *33, 2));
            } else if("UAH".equals(cur) && "USD".equals(currency)){
                account.setSum(Precision.round(sumAcc *27, 2));
            } else if("EUR".equals(cur) && "USD".equals(currency)){
                account.setSum(Precision.round(sumAcc *1.5, 2));
            } else if("EUR".equals(cur) && "UAH".equals(currency)){
                account.setSum(Precision.round(sumAcc /33, 2));
            } else if("USD".equals(cur) && "EUR".equals(currency)){
                account.setSum(Precision.round(sumAcc /1.5, 2));
            } else if("USD".equals(cur) && "UAH".equals(currency)){
                account.setSum(Precision.round(sumAcc /27, 2));
            }
        } else {

            account.setSum(Precision.round(sumAcc, 2));
        }
    }

    private void view(Query query) {
        List<Account> accounts = query.getResultList();
        System.out.println("id\t" + "number\t" + "sum\t" + "currency");
        for (Account account : accounts) {
            System.out.println(account.getId() + "\t"
                             + account.getNumber() + "\t"
                             + account.getSum() + "\t"
                             + account.getCurrency());
            System.out.println();
        }
    }
}
