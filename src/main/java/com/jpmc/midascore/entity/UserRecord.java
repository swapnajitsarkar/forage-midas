package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_records")
public class UserRecord {

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float balance;

    @OneToMany(mappedBy = "sender")
    private Set<TransactionRecord> sentTransactions;

    @OneToMany(mappedBy = "recipient")
    private Set<TransactionRecord> receivedTransactions;

    protected UserRecord() {
    }

    public UserRecord(long id, String name, float balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public UserRecord(String name, float balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', balance='%f']", id, name, balance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setBalance(double balance) {
        this.balance = (float) balance;
    }

    public Set<TransactionRecord> getSentTransactions() {
        return sentTransactions;
    }

    public Set<TransactionRecord> getReceivedTransactions() {
        return receivedTransactions;
    }
}
