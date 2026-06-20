package com.enviro.assessment.junior.tadii;

import com.enviro.assessment.junior.tadii.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.tadii.exception.BusinessRuleException;
import com.enviro.assessment.junior.tadii.model.InvestmentProduct;
import com.enviro.assessment.junior.tadii.model.Investor;
import com.enviro.assessment.junior.tadii.repository.InvestmentProductRepository;
import com.enviro.assessment.junior.tadii.repository.InvestorRepository;
import com.enviro.assessment.junior.tadii.repository.WithdrawalNoticeRepository;
import com.enviro.assessment.junior.tadii.service.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock private WithdrawalNoticeRepository withdrawalRepo;
    @Mock private InvestorRepository investorRepo;
    @Mock private InvestmentProductRepository productRepo;

    @InjectMocks private WithdrawalService withdrawalService;

    private Investor youngInvestor;
    private Investor seniorInvestor;
    private InvestmentProduct retirementProduct;
    private InvestmentProduct savingsProduct;

    @BeforeEach
    void setUp() {
        // Young investor (age 35)
        youngInvestor = new Investor();
        youngInvestor.setId(1L);
        youngInvestor.setName("Lindiwe Dlamini");
        youngInvestor.setDateOfBirth(LocalDate.now().minusYears(35));

        // Senior investor (age 73)
        seniorInvestor = new Investor();
        seniorInvestor.setId(2L);
        seniorInvestor.setName("Robert Khoza");
        seniorInvestor.setDateOfBirth(LocalDate.now().minusYears(73));

        // Retirement product belonging to youngInvestor
        retirementProduct = new InvestmentProduct();
        retirementProduct.setId(10L);
        retirementProduct.setProductType(InvestmentProduct.ProductType.RETIREMENT);
        retirementProduct.setBalance(new BigDecimal("100000.00"));
        retirementProduct.setInvestor(youngInvestor);

        // Savings product belonging to youngInvestor
        savingsProduct = new InvestmentProduct();
        savingsProduct.setId(11L);
        savingsProduct.setProductType(InvestmentProduct.ProductType.SAVINGS);
        savingsProduct.setBalance(new BigDecimal("50000.00"));
        savingsProduct.setInvestor(youngInvestor);
    }

    // ── Business Rule 1: Age check ───────────────────────────────────────────

    @Test
    @DisplayName("Should reject retirement withdrawal if investor is under 65")
    void shouldRejectRetirementWithdrawalIfUnder65() {
        when(investorRepo.findById(1L)).thenReturn(Optional.of(youngInvestor));
        when(productRepo.findById(10L)).thenReturn(Optional.of(retirementProduct));

        WithdrawalRequestDTO req = new WithdrawalRequestDTO();
        req.setInvestorId(1L);
        req.setProductId(10L);
        req.setAmount(new BigDecimal("10000.00"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(req));

        assertTrue(ex.getMessage().contains("Retirement withdrawals are only allowed"));
    }

    // ── Business Rule 2: Cannot exceed balance ───────────────────────────────

    @Test
    @DisplayName("Should reject withdrawal exceeding balance")
    void shouldRejectWithdrawalExceedingBalance() {
        when(investorRepo.findById(1L)).thenReturn(Optional.of(youngInvestor));
        when(productRepo.findById(11L)).thenReturn(Optional.of(savingsProduct));

        WithdrawalRequestDTO req = new WithdrawalRequestDTO();
        req.setInvestorId(1L);
        req.setProductId(11L);
        req.setAmount(new BigDecimal("60000.00")); // exceeds balance of 50000

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(req));

        assertTrue(ex.getMessage().contains("exceeds available balance"));
    }

    // ── Business Rule 3: 90% cap ─────────────────────────────────────────────

    @Test
    @DisplayName("Should reject withdrawal exceeding 90% of balance")
    void shouldRejectWithdrawalExceeding90Percent() {
        when(investorRepo.findById(1L)).thenReturn(Optional.of(youngInvestor));
        when(productRepo.findById(11L)).thenReturn(Optional.of(savingsProduct));

        WithdrawalRequestDTO req = new WithdrawalRequestDTO();
        req.setInvestorId(1L);
        req.setProductId(11L);
        req.setAmount(new BigDecimal("46000.00")); // 92% of 50000

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(req));

        assertTrue(ex.getMessage().contains("90% limit"));
    }

    // ── Happy path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should allow valid savings withdrawal")
    void shouldAllowValidSavingsWithdrawal() {
        when(investorRepo.findById(1L)).thenReturn(Optional.of(youngInvestor));
        when(productRepo.findById(11L)).thenReturn(Optional.of(savingsProduct));
        when(withdrawalRepo.save(any())).thenAnswer(inv -> {
            var w = inv.getArgument(0, com.enviro.assessment.junior.tadii.model.WithdrawalNotice.class);
            w.setId(99L);
            if (w.getCreatedAt() == null) w.setCreatedAt(java.time.LocalDateTime.now());
            return w;
        });

        WithdrawalRequestDTO req = new WithdrawalRequestDTO();
        req.setInvestorId(1L);
        req.setProductId(11L);
        req.setAmount(new BigDecimal("10000.00")); // 20% — valid

        var result = withdrawalService.createWithdrawal(req);

        assertNotNull(result);
        assertEquals(new BigDecimal("40000.00"), result.getBalanceAfterWithdrawal());
    }
}
