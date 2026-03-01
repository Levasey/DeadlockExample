package com.mikhailsazhin.deadlockexample;

public class Account {
    private int balance = 10000;

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }

    public static  void transfer(Account from, Account to, int amount) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Transfer amount cannot be negative");
        }
        if (from == to) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        from.withdraw(amount);
        to.deposit(amount);
    }
}
