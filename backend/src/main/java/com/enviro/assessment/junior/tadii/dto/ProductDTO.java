package com.enviro.assessment.junior.tadii.dto;

import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String productName;
    private InvestmentProduct.ProductType productType;
    private BigDecimal balance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public InvestmentProduct.ProductType getProductType() { return productType; }
    public void setProductType(InvestmentProduct.ProductType productType) { this.productType = productType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}