package com.cheque.online_cheque_book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChequeBookRequestRepository
        extends JpaRepository<ChequeBookRequest, Long> {

    List<ChequeBookRequest> findByAccountId(Long accountId);

    List<ChequeBookRequest> findByStatus(String status);
}