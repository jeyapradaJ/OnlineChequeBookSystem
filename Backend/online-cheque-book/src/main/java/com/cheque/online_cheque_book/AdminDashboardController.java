package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final CustomerRepository customerRepository;
    private final ChequeBookRequestRepository requestRepository;

    public AdminDashboardController(
            CustomerRepository customerRepository,
            ChequeBookRequestRepository requestRepository) {

        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public ResponseEntity<?> getDashboardData() {

        List<Customer> customers =
                customerRepository.findAll();

        List<ChequeBookRequest> requests =
                requestRepository.findAll();

        Map<String, Object> dashboard =
                new HashMap<>();

        dashboard.put("totalCustomers", customers.size());
        dashboard.put("totalRequests", requests.size());
        dashboard.put("requests", requests);

        return ResponseEntity.ok(dashboard);
    }
}