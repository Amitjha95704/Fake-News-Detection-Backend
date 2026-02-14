package com.truthlens.controller;

import com.truthlens.model.request.VerificationRequest;
import com.truthlens.model.response.VerificationResponse;
import com.truthlens.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vite frontend
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public VerificationResponse verify(@RequestBody VerificationRequest request) {
        return verificationService.verify(request);
    }
}
