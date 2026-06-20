package com.enviro.assessment.junior.tadii.service;

import com.enviro.assessment.junior.tadii.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.tadii.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.tadii.exception.BusinessRuleException;
import com.enviro.assessment.junior.tadii.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import com.enviro.assessment.junior.tadii.model.Investor;
import com.enviro.assessment.junior.tadii.model.WithdrawalNotice;
import com.enviro.assessment.junior.tadii.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.tadii.repository.InvestorRepository;
import com.enviro.assessment.junior.tadii.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WithdrawalService {

    private static final BigDecimal MAX_WITHDRAWAL_PERCENT = new BigDecimal("0.90");
    private static final int RETIREMENT_AGE_THRESHOLD = 65;

    private final WithdrawalNoticeRepository withdrawalRepo;
    private final InvestorRepository investorRepo;
    private final InvestmentProductRepository productRepo;

    public WithdrawalService(WithdrawalNoticeRepository withdrawalRepo,
                             InvestorRepository investorRepo,
                             InvestmentProductRepository productRepo) {
        this.withdrawalRepo = withdrawalRepo;
        this.investorRepo = investorRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public WithdrawalResponseDTO createWithdrawal(WithdrawalRequestDTO request) {
        Investor investor = investorRepo.findById(request.getInvestorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with ID: " + request.getInvestorId()));

        InvestmentProduct product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        if (!product.getInvestor().getId().equals(investor.getId())) {
            throw new BusinessRuleException("Product does not belong to this investor");
        }

        if (product.getProductType() == InvestmentProduct.ProductType.RETIREMENT
                && investor.getAge() <= RETIREMENT_AGE_THRESHOLD) {
            throw new BusinessRuleException(
                    "Retirement withdrawals are only allowed for investors older than "
                            + RETIREMENT_AGE_THRESHOLD + " years. Investor age: " + investor.getAge());
        }

        BigDecimal balance = product.getBalance();
        BigDecimal amount = request.getAmount();

        if (amount.compareTo(balance) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (R" + amount + ") exceeds available balance (R" + balance + ")");
        }

        BigDecimal maxAllowed = balance.multiply(MAX_WITHDRAWAL_PERCENT).setScale(2, RoundingMode.HALF_DOWN);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (R" + amount + ") exceeds 90% limit (R" + maxAllowed + ")");
        }

        BigDecimal newBalance = balance.subtract(amount);
        product.setBalance(newBalance);
        productRepo.save(product);

        WithdrawalNotice notice = new WithdrawalNotice();
        notice.setInvestor(investor);
        notice.setProduct(product);
        notice.setAmount(amount);
        notice.setBalanceAfterWithdrawal(newBalance);
        notice.setStatus(WithdrawalNotice.WithdrawalStatus.APPROVED);
        withdrawalRepo.save(notice);

        return toResponseDTO(notice);
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getWithdrawalsForInvestor(Long investorId) {
        if (!investorRepo.existsById(investorId)) {
            throw new ResourceNotFoundException("Investor not found with ID: " + investorId);
        }
        return withdrawalRepo.findByInvestorId(investorId).stream()
                .map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getAllWithdrawals(LocalDateTime from, LocalDateTime to) {
        List<WithdrawalNotice> notices = (from != null && to != null)
                ? withdrawalRepo.findByCreatedAtBetween(from, to)
                : withdrawalRepo.findAll();
        return notices.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private WithdrawalResponseDTO toResponseDTO(WithdrawalNotice n) {
        WithdrawalResponseDTO dto = new WithdrawalResponseDTO();
        dto.setId(n.getId());
        dto.setInvestorId(n.getInvestor().getId());
        dto.setInvestorName(n.getInvestor().getName());
        dto.setProductId(n.getProduct().getId());
        dto.setProductName(n.getProduct().getProductName());
        dto.setProductType(n.getProduct().getProductType().name());
        dto.setAmount(n.getAmount());
        dto.setBalanceAfterWithdrawal(n.getBalanceAfterWithdrawal());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setStatus(n.getStatus());
        return dto;
    }
}