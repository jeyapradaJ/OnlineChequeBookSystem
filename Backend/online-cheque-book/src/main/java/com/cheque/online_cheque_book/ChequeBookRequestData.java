package com.cheque.online_cheque_book;

public class ChequeBookRequestData {

    private int numberOfLeaves;
    private Long accountId;

    public ChequeBookRequestData() {
    }

    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
