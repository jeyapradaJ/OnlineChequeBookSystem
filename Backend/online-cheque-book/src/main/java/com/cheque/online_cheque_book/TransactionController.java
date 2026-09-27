package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public TransactionController(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            EmailService emailService) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    // Get transactions for an account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable Long accountId) {

        Account account =
                accountRepository.findById(accountId)
                        .orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest()
                    .body("Account not found");
        }

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdOrderByTransactionDateDesc(
                                accountId
                        );

        return ResponseEntity.ok(transactions);
    }

    // Create a transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(
            @RequestParam Long accountId,
            @RequestParam String transactionType,
            @RequestParam Double amount) {

        Account account =
                accountRepository.findById(accountId)
                        .orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest()
                    .body("Account not found");
        }

        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest()
                    .body("Amount must be greater than zero");
        }

        Double currentBalance =
                account.getBalance();

        if (transactionType.equals("MONEY_TRANSFER") ||
            transactionType.equals("CHEQUE_PAYMENT") ||
            transactionType.equals("WITHDRAWAL")) {

            if (currentBalance < amount) {
                return ResponseEntity.badRequest()
                        .body("Insufficient balance");
            }

            currentBalance =
                    currentBalance - amount;

        } else if (transactionType.equals("DEPOSIT")) {

            currentBalance =
                    currentBalance + amount;

        } else {

            return ResponseEntity.badRequest()
                    .body("Invalid transaction type");
        }

        account.setBalance(currentBalance);

        accountRepository.save(account);

        Transaction transaction =
                new Transaction();

        transaction.setTransactionType(
                transactionType
        );

        transaction.setAmount(amount);

        transaction.setBalanceAfterTransaction(
                currentBalance
        );

        transaction.setStatus("COMPLETED");

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setAccount(account);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // Send transaction email
        if (account.getCustomer() != null &&
            account.getCustomer().getEmail() != null) {

            String customerEmail =
                    account.getCustomer().getEmail();

            String customerName =
                    account.getCustomer().getName();

            String subject =
                    "Transaction Completed - ChequeBook";

            String message =
                    "Dear " + customerName + ",\n\n" +

                    "Your transaction has been completed successfully.\n\n" +

                    "Transaction ID: " +
                    savedTransaction.getId() + "\n" +

                    "Transaction Type: " +
                    transactionType + "\n" +

                    "Amount: ₹" +
                    amount + "\n" +

                    "Balance After Transaction: ₹" +
                    currentBalance + "\n" +

                    "Status: COMPLETED\n\n" +

                    "Thank you,\n" +
                    "ChequeBook - Online Cheque Book Request System";

            try {

                emailService.sendEmail(
                        customerEmail,
                        subject,
                        message
                );

            } catch (Exception e) {

                System.out.println(
                        "Transaction email could not be sent: "
                                + e.getMessage()
                );
            }
        }

        return ResponseEntity.ok(savedTransaction);
    }
}