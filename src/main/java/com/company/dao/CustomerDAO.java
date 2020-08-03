package com.company.dao;

import com.company.entities.Account;
import com.company.entities.Customer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private final List<Customer> customers = new ArrayList<>();

    public List<Customer> getCustomers() {
        return customers;
    }

    public CustomerDAO() {
        Customer c1 = new Customer("Customer1");
        Customer c2 = new Customer("Customer2");
        Customer c3 = new Customer("Customer3");
        Customer c4 = new Customer("Customer4");
        Customer c5 = new Customer("Customer5");

        Account account1 = new Account("11111111", 23456.34, "UAH");
        Account account2 = new Account("22222222", 567.89, "USD");
        Account account3 = new Account("33333333", 575763.44, "UAH");
        Account account4 = new Account("44444444", 123123.23, "UAH");
        Account account5 = new Account("55555555", 43.23, "EUR");
        Account account6 = new Account("66666666", 13.23, "EUR");
        Account account7 = new Account("7777777", 1546.47, "USD");
        Account account8 = new Account("88888888", 23.65, "EUR");
        Account account9 = new Account("99999999", 87566.54, "UAH");

        c1.addAccounts(account1);
        c1.addAccounts(account2);
        customers.add(c1);
        c2.addAccounts(account3);
        customers.add(c2);
        c3.addAccounts(account4);
        c3.addAccounts(account5);
        c3.addAccounts(account6);
        customers.add(c3);
        c4.addAccounts(account7);
        customers.add(c4);
        c5.addAccounts(account8);
        c5.addAccounts(account9);
        customers.add(c5);

    }

    public void createCustomers(EntityManager em) {
        em.getTransaction().begin();
        try{
            for (Customer customer : customers) {

                em.persist(customer);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public int selectCustomerByName(EntityManager em, String name) {
        Customer customer = new Customer();
        try{
            Query query = em.createQuery("SELECT c FROM Customer c WHERE c.name = :name");
            query.setParameter("name", name);

            customer = (Customer) query.getSingleResult();

        } catch (NoResultException e) {
            System.out.println("No clients found");
        }
        return customer.getId();

    }

}
