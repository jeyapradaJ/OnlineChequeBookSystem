package com.cheque.online_cheque_book;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Admin Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(
            @RequestBody Admin admin) {

        if (adminRepository.existsByEmail(admin.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Admin email is already registered");
        }

        admin.setPassword(
                passwordEncoder.encode(admin.getPassword())
        );

        Admin savedAdmin =
                adminRepository.save(admin);

        Map<String, Object> response =
                new HashMap<>();

        response.put("id", savedAdmin.getId());
        response.put("name", savedAdmin.getName());
        response.put("email", savedAdmin.getEmail());

        return ResponseEntity.ok(response);
    }

    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(
            @RequestBody Admin admin) {

        Admin existingAdmin =
                adminRepository.findByEmail(admin.getEmail())
                        .orElse(null);

        if (existingAdmin == null) {
            return ResponseEntity.status(401)
                    .body("Invalid admin email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        admin.getPassword(),
                        existingAdmin.getPassword()
                );

        if (!passwordMatches) {
            return ResponseEntity.status(401)
                    .body("Invalid admin email or password");
        }

        Map<String, Object> response =
                new HashMap<>();

        response.put("message", "Admin login successful");
        response.put("id", existingAdmin.getId());
        response.put("name", existingAdmin.getName());
        response.put("email", existingAdmin.getEmail());

        return ResponseEntity.ok(response);
    }
}
