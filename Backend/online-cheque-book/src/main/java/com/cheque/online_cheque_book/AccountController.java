package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountController(
            AccountRepository accountRepository,
            CustomerRepository customerRepository) {

        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    // Create bank account
    @PostMapping
    public ResponseEntity<?> createAccount(
            @RequestBody AccountRequest request) {

        Customer customer =
                customerRepository.findById(request.getCustomerId())
                        .orElse(null);

        if (customer == null) {
            return ResponseEntity.badRequest()
                    .body("Customer not found");
        }

        if (accountRepository
                .findByAccountNumber(request.getAccountNumber())
                .isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Account number already exists");
        }

        Account account = new Account();

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);

        Account savedAccount =
                accountRepository.save(account);

        return ResponseEntity.ok(createAccountResponse(savedAccount));
    }

    // Get customer accounts
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerAccounts(
            @PathVariable Long customerId) {

        List<Account> accounts =
                accountRepository.findByCustomerId(customerId);

        List<Map<String, Object>> response =
                accounts.stream()
                        .map(this::createAccountResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Create safe account response
    private Map<String, Object> createAccountResponse(
            Account account) {

        Map<String, Object> response =
                new HashMap<>();

        response.put("id", account.getId());
        response.put(
                "accountNumber",
                account.getAccountNumber()
        );
        response.put(
                "accountType",
                account.getAccountType()
        );
        response.put(
                "balance",
                account.getBalance()
        );

        if (account.getCustomer() != null) {
            response.put(
                    "customerId",
                    account.getCustomer().getId()
            );
        }

        return response;
    }
}