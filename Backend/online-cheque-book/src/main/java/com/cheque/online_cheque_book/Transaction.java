package com.cheque.online_cheque_book;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionType;

    private Double amount;

    private Double balanceAfterTransaction;

    private String status;

    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Transaction() {
    }

    public Transaction(
            String transactionType,
            Double amount,
            Double balanceAfterTransaction,
            String status,
            LocalDateTime transactionDate,
            Account account) {

        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfterTransaction =
                balanceAfterTransaction;
        this.status = status;
        this.transactionDate =
                transactionDate;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType =
                transactionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(
            Double balanceAfterTransaction) {

        this.balanceAfterTransaction =
                balanceAfterTransaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(
            LocalDateTime transactionDate) {

        this.transactionDate =
                transactionDate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}