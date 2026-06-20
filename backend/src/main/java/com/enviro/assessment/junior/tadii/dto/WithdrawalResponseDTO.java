package com.enviro.assessment.junior.tadii.dto;

import com.enviro.assessment.junior.tadii.model.WithdrawalNotice;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalResponseDTO {
    private Long id;
    private Long investorId;
    private String investorName;
    private Long productId;
    private String productName;
    private String productType;
    private BigDecimal amount;
    private BigDecimal balanceAfterWithdrawal;
    private LocalDateTime createdAt;
    private WithdrawalNotice.WithdrawalStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }

    public String getInvestorName() { return investorName; }
    public void setInvestorName(String investorName) { this.investorName = investorName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBalanceAfterWithdrawal() { return balanceAfterWithdrawal; }
    public void setBalanceAfterWithdrawal(BigDecimal b) { this.balanceAfterWithdrawal = b; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public WithdrawalNotice.WithdrawalStatus getStatus() { return status; }
    public void setStatus(WithdrawalNotice.WithdrawalStatus status) { this.status = status; }
}