/**
 * Copyright 2011 Security Compass
 */

package com.securitycompass.androidlabs.basicencryptionsolution;

/** 
 * @Author Ewan Sinclair
 * Represents a bank account */
public class Account {

    private int accountNumber;
    private String accountType;
    private double balance;

    /** Creates a new Account with the given parameters 
     * @param accountNumber The account number
     * @param accountType The type of account
     * @param balance The balance held in the account*/
    public Account(int accountNumber, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    /** Returns the account type 
     * @return The type of Account*/
    public String getAccountType() {
        return accountType;
    }

    /** Sets the account type 
     * @param accountType The type of Account to set*/
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /** Returns the balance of the Account 
     * @return The balance of the Account*/
    public double getBalance() {
        return balance;
    }

    /** Sets the balance of the Account 
     * @param balance The balance to set for the Account*/
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /** Sets the account number 
     * @param accountNumber The account number to set*/
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    /** Returns the account number 
     * @return The account number for this Account*/
    public int getAccountNumber() {
        return accountNumber;
    }
    
    /** Returns a String representation of the Account 
     * @return A String representation of this Account*/
    public String toString(){
        return (accountNumber + " (" + accountType + "): " + balance); 
    }

}
