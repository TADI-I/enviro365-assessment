package com.enviro.assessment.junior.tadii.controller;

import com.enviro.assessment.junior.tadii.dto.ApiResponse;
import com.enviro.assessment.junior.tadii.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.tadii.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.tadii.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@CrossOrigin(origins = "*")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WithdrawalResponseDTO>> createWithdrawal(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO result = withdrawalService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Withdrawal notice created successfully", result));
    }

    @GetMapping("/investor/{investorId}")
    public ResponseEntity<ApiResponse<List<WithdrawalResponseDTO>>> getByInvestor(@PathVariable Long investorId) {
        return ResponseEntity.ok(ApiResponse.ok(withdrawalService.getWithdrawalsForInvestor(investorId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WithdrawalResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(withdrawalService.getAllWithdrawals(null, null)));
    }
}