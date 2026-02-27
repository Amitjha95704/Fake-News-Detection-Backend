//package com.truthlens.controller;
//
//import com.truthlens.dto.request.VerificationRequest;
//import com.truthlens.dto.response.VerificationResponse;
//import com.truthlens.service.VerificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/verify")
//@RequiredArgsConstructor
//public class VerificationController {
//
//    private final VerificationService verificationService;
//
//    @PostMapping
//    public ResponseEntity<VerificationResponse> verify(@RequestBody VerificationRequest request) {
//        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
//            throw new IllegalArgumentException("Content cannot be empty");
//        }
//        return ResponseEntity.ok(verificationService.process(request));
//    }
//}

package com.truthlens.controller;

import com.truthlens.dto.request.VerificationRequest;
import com.truthlens.dto.response.VerificationResponse;
import com.truthlens.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<VerificationResponse> verify(@RequestBody VerificationRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        return ResponseEntity.ok(verificationService.process(request));
    }
}