package com.hdfc.minibank.main;

import com.hdfc.minibank.domain.Customer;
import com.hdfc.minibank.domain.Transaction;
import com.hdfc.minibank.domain.accounts.Account;
import com.hdfc.minibank.domain.accounts.CurrentAccount;
import com.hdfc.minibank.domain.accounts.SavingsAccount;
import com.hdfc.minibank.domain.enums.TransactionType;
import com.hdfc.minibank.exceptions.InsufficientBalanceException;
import com.hdfc.minibank.exceptions.InvalidAccountException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Customer> customers = new HashMap<>();
    private static final Map<String, Account> accounts = new HashMap<>();
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\nWelcome to HDFC Mini Bank!! ");
            System.out.println("1. Register New Customer");
            System.out.println("2. Create Account");
            System.out.println("3. Perform Transactions");
            System.out.println("4. View Account Details");
            System.out.println("5. View Transaction History");
            System.out.println("6. Simulate Concurrent Transfers");
            System.out.println("7. Demo Mode (Complete Flow)");
            System.out.println("8. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> createAccount();
                case 3 -> performTransaction();
                case 4 -> viewAccountDetails();
                case 5 -> viewTransactionHistory();
                case 6 -> simulateConcurrentTransfers();
                case 7 -> demo();
                case 8 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerCustomer(){

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter DOB (YYYY-MM-DD): ");
        LocalDate dob = LocalDate.parse(scanner.nextLine());

        try{
            Customer customer = new Customer(name, email, phone, dob);
            customers.put(customer.getCustomerId(), customer);
            System.out.println("Customer registered successfully! ID: " + customer.getCustomerId());
        }catch (IllegalArgumentException e){
            System.out.println("Error: " +e.getMessage());
        }
    }

    private static void createAccount(){

        System.out.println("Enter customer ID: ");
        String customerId = scanner.nextLine();
        Customer customer = customers.get(customerId);

        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter initial balance: ");
        BigDecimal initialBalance = scanner.nextBigDecimal();
        scanner.nextLine(); // consume newline

        System.out.print("Enter account type (1. SAVINGS, 2. CURRENT): ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        Account account;
        if (typeChoice == 1) {
            account = new SavingsAccount(initialBalance);
        } else if (typeChoice == 2) {
            account = new CurrentAccount(initialBalance);
        } else {
            System.out.println("Invalid type.");
            return;
        }

        accounts.put(account.getAccountNumber(), account);
        System.out.println("Account created successfully! Account No: " + account.getAccountNumber());

    }

    private static void performTransaction(){

        System.out.print("Enter account number: ");
        String accountNo = scanner.nextLine();
        Account account = accounts.get(accountNo);
        if (account == null) {
            System.out.println("Invalid account.");
            return;
        }

        System.out.println("Select transaction: 1. Deposit  2. Withdraw  3. Transfer");
        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter amount: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            switch (choice) {
                case 1 -> {
                    account.deposit(amount);
                    transactions.add(new Transaction(accountNo, TransactionType.DEPOSIT, amount));
                    System.out.println("Deposit successful.");
                }
                case 2 -> {
                    account.withdraw(amount);
                    transactions.add(new Transaction(accountNo, TransactionType.WITHDRAWAL, amount));
                    System.out.println("Withdrawal successful.");
                }
                case 3 -> {
                    System.out.print("Enter destination account number: ");
                    String toAccountNo = scanner.nextLine();
                    Account toAccount = accounts.get(toAccountNo);
                    if (toAccount == null) throw new InvalidAccountException("Target account not found.");

                    account.withdraw(amount);
                    toAccount.deposit(amount);
                    transactions.add(new Transaction(accountNo, TransactionType.TRANSFER, amount));
                    transactions.add(new Transaction(toAccountNo, TransactionType.DEPOSIT, amount));
                    System.out.println("Transfer successful.");
                }
                default -> System.out.println("Invalid choice.");
            }
        } catch (InsufficientBalanceException | InvalidAccountException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }

    }

    private static void viewAccountDetails(){

        System.out.print("Enter account number: ");
        String accountNo = scanner.nextLine();
        Account account = accounts.get(accountNo);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Account No: " + account.getAccountNumber());
        System.out.println("Balance: ₹" + account.getBalance());
        System.out.println("Interest: ₹" + account.getInterest());
        System.out.println("Min Balance: ₹" + account.getMinimumBalance());
        System.out.println("Interest Rate: " + account.getInterestRate() + "%");

    }

    private static void viewTransactionHistory(){

        System.out.print("Enter account number: ");
        String accountNo = scanner.nextLine();

        List<Transaction> history = transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNo))
                .sorted(Comparator.comparing(Transaction::getTimeStamp))
                .toList();

        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction t : history) {
            System.out.println(t.getType() + " - ₹" + t.getAmount() + " at " + t.getTimeStamp());
        }
    }

    private static void simulateConcurrentTransfers(){

        System.out.print("Enter source account number: ");
        String fromAccNo = scanner.nextLine();
        System.out.print("Enter destination account number: ");
        String toAccNo = scanner.nextLine();
        System.out.print("Enter amount to transfer concurrently: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();

        Account from = accounts.get(fromAccNo);
        Account to = accounts.get(toAccNo);

        if (from == null || to == null) {
            System.out.println("Invalid accounts.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            tasks.add(() -> {
                try {
                    from.withdraw(amount);
                    to.deposit(amount);
                    return true;
                } catch (InsufficientBalanceException e) {
                    System.out.println("Transfer failed: " + e.getMessage());
                    return false;
                }
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("Concurrent transfers completed.");
    }

    private static void demo(){

        System.out.println("\n=== Demo Mode - Complete Banking Flow ===");

        // Step 1: Register Customers
        Customer c1 = new Customer("Ravi Kumar", "ravi@example.com", "9876543210", LocalDate.of(1990, 5, 10));
        customers.put(c1.getCustomerId(), c1);
        System.out.println("Customers registered successfully");

        // Step 2: Create Accounts
        Account sAcc = new SavingsAccount(BigDecimal.valueOf(5000));
        Account cAcc = new CurrentAccount(BigDecimal.valueOf(2000));
        accounts.put(sAcc.getAccountNumber(), sAcc);
        accounts.put(cAcc.getAccountNumber(), cAcc);
        System.out.println("Accounts created successfully");

        // Step 3: Transactions
        sAcc.deposit(BigDecimal.valueOf(1000));
        transactions.add(new Transaction(sAcc.getAccountNumber(), TransactionType.DEPOSIT, BigDecimal.valueOf(1000)));

        try {
            sAcc.withdraw(BigDecimal.valueOf(800));
            transactions.add(new Transaction(sAcc.getAccountNumber(), TransactionType.WITHDRAWAL, BigDecimal.valueOf(800)));

            sAcc.withdraw(BigDecimal.valueOf(500));
            cAcc.deposit(BigDecimal.valueOf(500));
            transactions.add(new Transaction(sAcc.getAccountNumber(), TransactionType.TRANSFER, BigDecimal.valueOf(500)));
            transactions.add(new Transaction(cAcc.getAccountNumber(), TransactionType.DEPOSIT, BigDecimal.valueOf(500)));

            System.out.println("Transactions successful");
        } catch (InsufficientBalanceException e) {
            System.out.println("Transaction error: " + e.getMessage());
        }

        // Step 4: Display Account Details
        System.out.println("\nAccount details:");
        System.out.println("Savings Account: " + sAcc.getBalance() + " (Interest: " + sAcc.getInterest() + ")");
        System.out.println("Current Account: " + cAcc.getBalance() + " (Interest: " + cAcc.getInterest() + ")");

        // Step 5: View Transaction History
        System.out.println("\nTransaction history:");
        transactions.stream()
                .filter(t -> t.getAccountNumber().equals(sAcc.getAccountNumber()))
                .forEach(t -> System.out.println(t.getType() + " - " + t.getAmount() + " at " + t.getTimeStamp()));

        // Step 6: Polymorphism Demo
        System.out.println("\nDemonstrating polymorphism:");
        System.out.println("SavingsAccount - Min Balance: " + sAcc.getMinimumBalance() + ", Interest Rate: " + sAcc.getInterestRate());
        System.out.println("CurrentAccount - Min Balance: " + cAcc.getMinimumBalance() + ", Interest Rate: " + cAcc.getInterestRate());

        System.out.println("\n=== Demo completed successfully! ===");
    }
}