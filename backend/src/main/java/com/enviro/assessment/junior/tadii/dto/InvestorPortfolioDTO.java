package com.enviro.assessment.junior.tadii.dto;

import java.math.BigDecimal;
import java.util.List;

public class InvestorPortfolioDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private List<ProductDTO> products;
    private BigDecimal totalBalance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }

    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
}