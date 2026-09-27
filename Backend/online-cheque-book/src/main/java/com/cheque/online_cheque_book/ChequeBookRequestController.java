package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cheque-requests")
@CrossOrigin(origins = "*")
public class ChequeBookRequestController {

    private final ChequeBookRequestRepository requestRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public ChequeBookRequestController(
            ChequeBookRequestRepository requestRepository,
            AccountRepository accountRepository,
            EmailService emailService) {

        this.requestRepository = requestRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    // Create cheque book request
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody ChequeBookRequestData requestData) {

        if (requestData.getNumberOfLeaves() != 25 &&
            requestData.getNumberOfLeaves() != 50 &&
            requestData.getNumberOfLeaves() != 100) {

            return ResponseEntity.badRequest()
                    .body("Number of leaves must be 25, 50, or 100");
        }

        Account account = accountRepository
                .findById(requestData.getAccountId())
                .orElse(null);

        if (account == null) {
            return ResponseEntity.badRequest()
                    .body("Account not found");
        }

        ChequeBookRequest request =
                new ChequeBookRequest();

        request.setNumberOfLeaves(
                requestData.getNumberOfLeaves()
        );

        request.setStatus("PENDING");

        request.setAccount(account);

        ChequeBookRequest savedRequest =
                requestRepository.save(request);

        // Send confirmation email
        if (account.getCustomer() != null &&
            account.getCustomer().getEmail() != null) {

            String customerEmail =
                    account.getCustomer().getEmail();

            String customerName =
                    account.getCustomer().getName();

            String subject =
                    "Cheque Book Request Submitted";

            String message =
                    "Dear " + customerName + ",\n\n" +

                    "Your cheque book request has been submitted successfully.\n\n" +

                    "Request ID: " + savedRequest.getId() + "\n" +
                    "Account Number: " +
                    account.getAccountNumber() + "\n" +
                    "Number of Leaves: " +
                    savedRequest.getNumberOfLeaves() + "\n" +
                    "Status: " +
                    savedRequest.getStatus() + "\n\n" +

                    "You can track the request status from your customer dashboard.\n\n" +

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
                        "Email could not be sent: "
                                + e.getMessage()
                );
            }
        }

        return ResponseEntity.ok(savedRequest);
    }

    // Get requests by account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getRequestsByAccount(
            @PathVariable Long accountId) {

        List<ChequeBookRequest> requests =
                requestRepository.findByAccountId(accountId);

        return ResponseEntity.ok(requests);
    }
}