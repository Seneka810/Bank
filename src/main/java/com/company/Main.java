package com.company;

import com.company.dao.AccountDAO;
import com.company.dao.CustomerDAO;
import com.company.entities.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;
    static CustomerDAO customer = new CustomerDAO();
    static AccountDAO account = new AccountDAO();
    static Set<String> currencies = new HashSet<>();
    static Set<String> customerNames = new HashSet<>();

    static {
        currencies.add("UAH");
        currencies.add("USD");
        currencies.add("EUR");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try{
            emf = Persistence.createEntityManagerFactory("JPABank");
            em = emf.createEntityManager();

            customer.createCustomers(em);
            List<Customer> customerList = customer.getCustomers();
            for (int i = 0; i < customerList.size(); i++) {
                customerNames.add(customerList.get(i).getName());
            }

            try{
                while (true) {
                    System.out.println("Enter name of Customer:");
                    String name = scanner.nextLine();
                    if(name.isEmpty()) {
                        break;
                    }
                    if(!customerNames.contains(name)) {
                        continue;
                    }
                    account.selectAccountsByCustomers(em, name);

                    System.out.println("To top up your account enter 1:");
                    System.out.println("To transfer money between accounts enter 2:");
                    System.out.println("To get total amount for your accounts in UAH enter 3:");

                    String s = scanner.nextLine();
                    while (true) {
                        if("1".equals(s)) {
                            System.out.println("Choose account id which you want top up:");
                            String idAcc = scanner.nextLine();
                            if(idAcc.isEmpty()){
                                break;
                            }
                            System.out.println("Enter one of currency from USD, EUR or UAH:");
                            String cur = scanner.nextLine();
                            if(!currencies.contains(cur)){
                                continue;
                            }
                            System.out.println("Enter sum top up account: ");
                            String sum = scanner.nextLine();
                            if(sum.isEmpty()) {
                                break;
                            }
                            account.topUpAccount(em, Integer.parseInt(idAcc), Double.parseDouble(sum), name, cur);
                        }

                        if("2".equals(s)) {
                            System.out.println("Enter id accounts from which you want withdraw money: ");
                            String from = scanner.nextLine();
                            System.out.println("Enter id accounts to which you want put money: ");
                            String to = scanner.nextLine();
                            System.out.println("Enter sum which you want transfer: ");
                            String sum = scanner.nextLine();
                            account.transferSum(em, name, Integer.parseInt(from), Integer.parseInt(to), Double.parseDouble(sum));
                            break;
                        }

                        if("3".equals(s)) {
                            System.out.println("\n" + "In your accounts is: " + account.getTotalAmount(em, name) + " UAH\n");
                            break;
                        }
                    }
                }
            } finally {
                scanner.close();
                em.close();
                emf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
