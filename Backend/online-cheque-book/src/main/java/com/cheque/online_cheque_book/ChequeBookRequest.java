package com.cheque.online_cheque_book;

import jakarta.persistence.*;

@Entity
@Table(name = "cheque_book_requests")
public class ChequeBookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numberOfLeaves;

    private String status;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public ChequeBookRequest() {
    }

    public ChequeBookRequest(int numberOfLeaves, String status, Account account) {
        this.numberOfLeaves = numberOfLeaves;
        this.status = status;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
