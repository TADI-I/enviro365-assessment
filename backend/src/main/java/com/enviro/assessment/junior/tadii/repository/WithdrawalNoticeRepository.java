package com.enviro.assessment.junior.tadii.repository;

import com.enviro.assessment.junior.tadii.model.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    List<WithdrawalNotice> findByInvestorId(Long investorId);
    List<WithdrawalNotice> findByInvestorIdAndCreatedAtBetween(Long investorId, LocalDateTime from, LocalDateTime to);
    List<WithdrawalNotice> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
