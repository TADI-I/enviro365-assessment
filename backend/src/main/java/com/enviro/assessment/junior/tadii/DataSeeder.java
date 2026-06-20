package com.enviro.assessment.junior.tadii;

import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import com.enviro.assessment.junior.tadii.model.Investor;
import com.enviro.assessment.junior.tadii.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.tadii.repository.InvestorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final InvestorRepository investorRepo;
    private final InvestmentProductRepository productRepo;

    public DataSeeder(InvestorRepository investorRepo, InvestmentProductRepository productRepo) {
        this.investorRepo = investorRepo;
        this.productRepo = productRepo;
    }

    @Override
    public void run(String... args) {
        Investor senior = new Investor();
        senior.setName("Robert Khoza");
        senior.setEmail("robert.khoza@email.com");
        senior.setDateOfBirth(LocalDate.of(1952, 3, 14));
        investorRepo.save(senior);

        InvestmentProduct p1 = new InvestmentProduct();
        p1.setProductName("Retirement Annuity Fund");
        p1.setProductType(InvestmentProduct.ProductType.RETIREMENT);
        p1.setBalance(new BigDecimal("850000.00"));
        p1.setInvestor(senior);
        productRepo.save(p1);

        InvestmentProduct p2 = new InvestmentProduct();
        p2.setProductName("Tax-Free Savings");
        p2.setProductType(InvestmentProduct.ProductType.SAVINGS);
        p2.setBalance(new BigDecimal("120000.00"));
        p2.setInvestor(senior);
        productRepo.save(p2);

        Investor young = new Investor();
        young.setName("Lindiwe Dlamini");
        young.setEmail("lindiwe.dlamini@email.com");
        young.setDateOfBirth(LocalDate.of(1990, 7, 22));
        investorRepo.save(young);

        InvestmentProduct p3 = new InvestmentProduct();
        p3.setProductName("Unit Trust");
        p3.setProductType(InvestmentProduct.ProductType.SAVINGS);
        p3.setBalance(new BigDecimal("45000.00"));
        p3.setInvestor(young);
        productRepo.save(p3);

        InvestmentProduct p4 = new InvestmentProduct();
        p4.setProductName("Retirement Pension Fund");
        p4.setProductType(InvestmentProduct.ProductType.RETIREMENT);
        p4.setBalance(new BigDecimal("200000.00"));
        p4.setInvestor(young);
        productRepo.save(p4);

        System.out.println("Enviro365 seed data loaded successfully.");
    }
}