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
            @RequestParam Double amount,
            @RequestParam(required = false) String beneficiaryAccount) {

        // Find sender account
        Account senderAccount =
                accountRepository.findById(accountId)
                        .orElse(null);

        if (senderAccount == null) {
            return ResponseEntity.badRequest()
                    .body("Sender account not found");
        }

        // Validate amount
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest()
                    .body("Amount must be greater than zero");
        }

        // =========================================================
        // MONEY TRANSFER
        // =========================================================
        if (transactionType.equals("MONEY_TRANSFER")) {

            // Beneficiary account number required
            if (beneficiaryAccount == null ||
                    beneficiaryAccount.trim().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("Please enter beneficiary account number");
            }

            // Find beneficiary account
            Account receiverAccount =
                    accountRepository
                            .findByAccountNumber(
                                    beneficiaryAccount.trim()
                            )
                            .orElse(null);

            if (receiverAccount == null) {
                return ResponseEntity.badRequest()
                        .body("Beneficiary account not found");
            }

            // Prevent self-transfer
            if (senderAccount.getId()
                    .equals(receiverAccount.getId())) {

                return ResponseEntity.badRequest()
                        .body("You cannot transfer money to your own account");
            }

            // Check sender balance
            if (senderAccount.getBalance() < amount) {
                return ResponseEntity.badRequest()
                        .body("Insufficient balance");
            }

            // -----------------------------------------------------
            // Deduct money from sender
            // -----------------------------------------------------
            Double senderNewBalance =
                    senderAccount.getBalance() - amount;

            senderAccount.setBalance(senderNewBalance);

            accountRepository.save(senderAccount);

            // -----------------------------------------------------
            // Add money to receiver
            // -----------------------------------------------------
            Double receiverNewBalance =
                    receiverAccount.getBalance() + amount;

            receiverAccount.setBalance(receiverNewBalance);

            accountRepository.save(receiverAccount);

            // -----------------------------------------------------
            // Sender transaction
            // -----------------------------------------------------
            Transaction senderTransaction =
                    new Transaction();

            senderTransaction.setTransactionType(
                    "MONEY_TRANSFER"
            );

            senderTransaction.setAmount(amount);

            senderTransaction.setBalanceAfterTransaction(
                    senderNewBalance
            );

            senderTransaction.setStatus("COMPLETED");

            senderTransaction.setTransactionDate(
                    LocalDateTime.now()
            );

            senderTransaction.setAccount(
                    senderAccount
            );

            Transaction savedSenderTransaction =
                    transactionRepository.save(
                            senderTransaction
                    );

            // -----------------------------------------------------
            // Receiver transaction
            // -----------------------------------------------------
            Transaction receiverTransaction =
                    new Transaction();

            receiverTransaction.setTransactionType(
                    "MONEY_RECEIVED"
            );

            receiverTransaction.setAmount(amount);

            receiverTransaction.setBalanceAfterTransaction(
                    receiverNewBalance
            );

            receiverTransaction.setStatus("COMPLETED");

            receiverTransaction.setTransactionDate(
                    LocalDateTime.now()
            );

            receiverTransaction.setAccount(
                    receiverAccount
            );

            transactionRepository.save(
                    receiverTransaction
            );

            // -----------------------------------------------------
            // Email to sender
            // -----------------------------------------------------
            if (senderAccount.getCustomer() != null &&
                    senderAccount.getCustomer().getEmail() != null) {

                String customerEmail =
                        senderAccount.getCustomer().getEmail();

                String customerName =
                        senderAccount.getCustomer().getName();

                String subject =
                        "Money Transfer Successful - ChequeBook";

                String message =
                        "Dear " + customerName + ",\n\n" +

                        "Your money transfer has been completed successfully.\n\n" +

                        "Transaction ID: " +
                        savedSenderTransaction.getId() + "\n" +

                        "Amount Transferred: ₹" +
                        amount + "\n" +

                        "Beneficiary Account: " +
                        beneficiaryAccount + "\n" +

                        "Balance After Transfer: ₹" +
                        senderNewBalance + "\n\n" +

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
                            "Sender email could not be sent: "
                                    + e.getMessage()
                    );
                }
            }

            // -----------------------------------------------------
            // Email to receiver
            // -----------------------------------------------------
            if (receiverAccount.getCustomer() != null &&
                    receiverAccount.getCustomer().getEmail() != null) {

                String customerEmail =
                        receiverAccount.getCustomer().getEmail();

                String customerName =
                        receiverAccount.getCustomer().getName();

                String subject =
                        "Money Received - ChequeBook";

                String message =
                        "Dear " + customerName + ",\n\n" +

                        "You have received money in your bank account.\n\n" +

                        "Amount Received: ₹" +
                        amount + "\n" +

                        "From Account: " +
                        senderAccount.getAccountNumber() + "\n" +

                        "Your New Balance: ₹" +
                        receiverNewBalance + "\n\n" +

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
                            "Receiver email could not be sent: "
                                    + e.getMessage()
                    );
                }
            }

            return ResponseEntity.ok(
                    savedSenderTransaction
            );
        }

        // =========================================================
        // OTHER TRANSACTIONS
        // =========================================================

        Double currentBalance =
                senderAccount.getBalance();

        if (transactionType.equals("CHEQUE_PAYMENT") ||
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

        senderAccount.setBalance(currentBalance);

        accountRepository.save(senderAccount);

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

        transaction.setAccount(
                senderAccount
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // Send transaction email
        if (senderAccount.getCustomer() != null &&
                senderAccount.getCustomer().getEmail() != null) {

            String customerEmail =
                    senderAccount.getCustomer().getEmail();

            String customerName =
                    senderAccount.getCustomer().getName();

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
                    currentBalance + "\n\n" +

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