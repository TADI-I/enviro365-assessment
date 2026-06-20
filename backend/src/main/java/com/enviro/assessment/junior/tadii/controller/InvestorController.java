package com.enviro.assessment.junior.tadii.controller;

import com.enviro.assessment.junior.tadii.dto.ApiResponse;
import com.enviro.assessment.junior.tadii.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.tadii.service.InvestorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investors")
@CrossOrigin(origins = "*")
public class InvestorController {

    private final InvestorService investorService;

    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestorPortfolioDTO>>> getAllInvestors() {
        return ResponseEntity.ok(ApiResponse.ok(investorService.getAllInvestors()));
    }

    @GetMapping("/{id}/portfolio")
    public ResponseEntity<ApiResponse<InvestorPortfolioDTO>> getPortfolio(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(investorService.getPortfolio(id)));
    }
}