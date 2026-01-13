package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "incentives")
public class Incentive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecord transaction;

    @Column(nullable = false)
    private float amount;

    protected Incentive() {
    }

    public Incentive(TransactionRecord transaction, float amount) {
        this.transaction = transaction;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public TransactionRecord getTransaction() {
        return transaction;
    }

    public float getAmount() {
        return amount;
    }
}