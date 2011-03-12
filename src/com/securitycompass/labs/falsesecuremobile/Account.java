package com.securitycompass.labs.falsesecuremobile;

/** Represents a bank account */
public class Account {

    private int accountNumber;
    private String accountType;
    private double balance;

    /** Creates a new Account with the given parameters */
    public Account(int accountNumber, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    /** Returns the account type */
    public String getAccountType() {
        return accountType;
    }

    /** Sets the account type */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /** Returns the balance of the account */
    public double getBalance() {
        return balance;
    }

    /** Sets the balance of the account */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /** Sets the account number */
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    /** Returns the account number */
    public int getAccountNumber() {
        return accountNumber;
    }

}
