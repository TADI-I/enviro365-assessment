package com.enviro.assessment.junior.tadii.service;

import com.enviro.assessment.junior.tadii.dto.InvestorPortfolioDTO;
import com.enviro.assessment.junior.tadii.dto.ProductDTO;
import com.enviro.assessment.junior.tadii.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.tadii.model.Investor;
import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import com.enviro.assessment.junior.tadii.repository.InvestorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class InvestorService {

    private final InvestorRepository investorRepository;

    public InvestorService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    public InvestorPortfolioDTO getPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with ID: " + investorId));

        List<ProductDTO> products = investor.getProducts().stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());

        BigDecimal total = products.stream()
                .map(ProductDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvestorPortfolioDTO dto = new InvestorPortfolioDTO();
        dto.setId(investor.getId());
        dto.setName(investor.getName());
        dto.setEmail(investor.getEmail());
        dto.setAge(investor.getAge());
        dto.setProducts(products);
        dto.setTotalBalance(total);
        return dto;
    }

    public List<InvestorPortfolioDTO> getAllInvestors() {
        return investorRepository.findAll().stream()
                .map(inv -> getPortfolio(inv.getId()))
                .collect(Collectors.toList());
    }

    private ProductDTO toProductDTO(InvestmentProduct p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setProductName(p.getProductName());
        dto.setProductType(p.getProductType());
        dto.setBalance(p.getBalance());
        return dto;
    }
}