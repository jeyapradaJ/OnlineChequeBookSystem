package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final ChequeBookRequestRepository requestRepository;

    public CustomerController(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            AccountRepository accountRepository,
            ChequeBookRequestRepository requestRepository) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.requestRepository = requestRepository;
    }

    // Customer Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(
            @RequestBody Customer customer) {

        if (customerRepository.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email is already registered");
        }

        customer.setPassword(
                passwordEncoder.encode(customer.getPassword())
        );

        Customer savedCustomer =
                customerRepository.save(customer);

        Map<String, Object> response =
                new HashMap<>();

        response.put("id", savedCustomer.getId());
        response.put("name", savedCustomer.getName());
        response.put("email", savedCustomer.getEmail());
        response.put("phone", savedCustomer.getPhone());

        return ResponseEntity.ok(response);
    }

    // Customer Login
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(
            @RequestBody Customer customer) {

        Customer existingCustomer =
                customerRepository.findByEmail(customer.getEmail())
                        .orElse(null);

        if (existingCustomer == null) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        customer.getPassword(),
                        existingCustomer.getPassword()
                );

        if (!passwordMatches) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        Map<String, Object> response =
                new HashMap<>();

        response.put("message", "Login successful");
        response.put("id", existingCustomer.getId());
        response.put("name", existingCustomer.getName());
        response.put("email", existingCustomer.getEmail());
        response.put("phone", existingCustomer.getPhone());

        return ResponseEntity.ok(response);
    }

    // Get all customers
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {

        List<Customer> customers =
                customerRepository.findAll();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Customer customer : customers) {

            Map<String, Object> customerData =
                    new HashMap<>();

            customerData.put("id", customer.getId());
            customerData.put("name", customer.getName());
            customerData.put("email", customer.getEmail());
            customerData.put("phone", customer.getPhone());

            response.add(customerData);
        }

        return ResponseEntity.ok(response);
    }

    // Customer Dashboard
    @GetMapping("/{customerId}/dashboard")
    public ResponseEntity<?> getCustomerDashboard(
            @PathVariable Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElse(null);

        if (customer == null) {
            return ResponseEntity.badRequest()
                    .body("Customer not found");
        }

        List<Account> accountList =
                accountRepository.findByCustomerId(customerId);

        List<Map<String, Object>> accounts =
                new ArrayList<>();

        List<ChequeBookRequest> chequeBookRequests =
                new ArrayList<>();

        for (Account account : accountList) {

            Map<String, Object> accountData =
                    new HashMap<>();

            accountData.put("id", account.getId());
            accountData.put(
                    "accountNumber",
                    account.getAccountNumber()
            );
            accountData.put(
                    "accountType",
                    account.getAccountType()
            );
            accountData.put(
                    "balance",
                    account.getBalance()
            );
            accountData.put(
                    "customerId",
                    customerId
            );

            accounts.add(accountData);

            List<ChequeBookRequest> accountRequests =
                    requestRepository.findByAccountId(
                            account.getId()
                    );

            chequeBookRequests.addAll(accountRequests);
        }

        CustomerDashboardData dashboard =
                new CustomerDashboardData(
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        accounts,
                        chequeBookRequests
                );

        return ResponseEntity.ok(dashboard);
    }
}