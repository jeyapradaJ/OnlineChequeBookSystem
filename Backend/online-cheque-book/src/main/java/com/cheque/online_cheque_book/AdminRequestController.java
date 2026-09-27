package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/cheque-requests")
@CrossOrigin(origins = "*")
public class AdminRequestController {

    private final ChequeBookRequestRepository requestRepository;
    private final EmailService emailService;

    public AdminRequestController(
            ChequeBookRequestRepository requestRepository,
            EmailService emailService) {

        this.requestRepository = requestRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests() {

        List<ChequeBookRequest> requests =
                requestRepository.findAll();

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(
            @PathVariable Long requestId) {

        ChequeBookRequest request =
                requestRepository.findById(requestId)
                        .orElse(null);

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body("Cheque book request not found");
        }

        return ResponseEntity.ok(request);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRequestsByStatus(
            @PathVariable String status) {

        if (!isValidStatus(status)) {
            return ResponseEntity.badRequest()
                    .body("Invalid status");
        }

        List<ChequeBookRequest> requests =
                requestRepository.findByStatus(status);

        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long requestId,
            @RequestParam String status) {

        ChequeBookRequest request =
                requestRepository.findById(requestId)
                        .orElse(null);

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body("Cheque book request not found");
        }

        if (!isValidStatus(status)) {
            return ResponseEntity.badRequest()
                    .body("Invalid status");
        }

        request.setStatus(status);

        ChequeBookRequest updatedRequest =
                requestRepository.save(request);

        // Send status update email to customer
        if (request.getAccount() != null &&
            request.getAccount().getCustomer() != null) {

            Customer customer =
                    request.getAccount().getCustomer();

            String customerEmail =
                    customer.getEmail();

            String customerName =
                    customer.getName();

            String subject =
                    "Cheque Book Request Status Updated";

            String message =
                    "Dear " + customerName + ",\n\n" +

                    "Your cheque book request status has been updated.\n\n" +

                    "Request ID: " + request.getId() + "\n" +
                    "Account Number: " +
                    request.getAccount().getAccountNumber() + "\n" +
                    "Number of Leaves: " +
                    request.getNumberOfLeaves() + "\n" +
                    "New Status: " +
                    request.getStatus() + "\n\n" +

                    "Please check your customer dashboard for more details.\n\n" +

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
                        "Status email could not be sent: "
                                + e.getMessage()
                );
            }
        }

        return ResponseEntity.ok(updatedRequest);
    }

    // View complete cheque book request details
    @GetMapping("/details")
    public ResponseEntity<?> getCompleteRequestDetails() {

        List<ChequeBookRequest> requests =
                requestRepository.findAll();

        List<Map<String, Object>> response =
                new java.util.ArrayList<>();

        for (ChequeBookRequest request : requests) {

            Map<String, Object> data =
                    new java.util.HashMap<>();

            Account account = request.getAccount();

            if (account != null) {

                Customer customer =
                        account.getCustomer();

                data.put("requestId", request.getId());
                data.put("accountNumber",
                        account.getAccountNumber());
                data.put("accountType",
                        account.getAccountType());
                data.put("numberOfLeaves",
                        request.getNumberOfLeaves());
                data.put("status",
                        request.getStatus());

                if (customer != null) {

                    data.put("customerName",
                            customer.getName());

                    data.put("customerEmail",
                            customer.getEmail());

                    data.put("customerPhone",
                            customer.getPhone());
                }
            }

            response.add(data);
        }

        return ResponseEntity.ok(response);
    }

    private boolean isValidStatus(String status) {

        return status.equals("PENDING") ||
               status.equals("PROCESSING") ||
               status.equals("APPROVED") ||
               status.equals("DISPATCHED") ||
               status.equals("COMPLETED") ||
               status.equals("REJECTED");
    }
}