package com.cheque.online_cheque_book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Online Cheque Book System Backend is Running Successfully!";
    }
}