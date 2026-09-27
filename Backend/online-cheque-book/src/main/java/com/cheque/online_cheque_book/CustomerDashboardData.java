package com.cheque.online_cheque_book;

import java.util.List;
import java.util.Map;

public class CustomerDashboardData {

    private Long customerId;
    private String name;
    private String email;
    private String phone;

    private List<Map<String, Object>> accounts;

    private List<ChequeBookRequest> chequeBookRequests;


    public CustomerDashboardData() {
    }


    public CustomerDashboardData(
            Long customerId,
            String name,
            String email,
            String phone,
            List<Map<String, Object>> accounts,
            List<ChequeBookRequest> chequeBookRequests) {

        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accounts = accounts;
        this.chequeBookRequests = chequeBookRequests;
    }


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public List<Map<String, Object>> getAccounts() {
        return accounts;
    }

    public void setAccounts(
            List<Map<String, Object>> accounts) {

        this.accounts = accounts;
    }


    public List<ChequeBookRequest> getChequeBookRequests() {
        return chequeBookRequests;
    }

    public void setChequeBookRequests(
            List<ChequeBookRequest> chequeBookRequests) {

        this.chequeBookRequests =
                chequeBookRequests;
    }
}