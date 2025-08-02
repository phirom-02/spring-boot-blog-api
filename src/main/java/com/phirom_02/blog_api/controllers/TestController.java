package com.phirom_02.blog_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndpoint(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok("Authenticated as: " + userId);
    }
}
