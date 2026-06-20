package com.enviro.assessment.junior.tadii.repository;

import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
    List<InvestmentProduct> findByInvestorId(Long investorId);
}
