package com.enviro.assessment.junior.tadii.service;

import com.enviro.assessment.junior.tadii.dto.WithdrawalResponseDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportService {

    private final WithdrawalService withdrawalService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CsvExportService(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    public byte[] exportWithdrawals(Long investorId, LocalDateTime from, LocalDateTime to) {
        List<WithdrawalResponseDTO> data = investorId != null
                ? withdrawalService.getWithdrawalsForInvestor(investorId)
                : withdrawalService.getAllWithdrawals(from, to);

        StringWriter sw = new StringWriter();
        try (CSVWriter writer = new CSVWriter(sw)) {
            writer.writeNext(new String[]{"ID","Investor ID","Investor Name","Product ID","Product Name","Product Type","Amount (R)","Balance After (R)","Date","Status"});
            for (WithdrawalResponseDTO w : data) {
                writer.writeNext(new String[]{
                        String.valueOf(w.getId()),
                        String.valueOf(w.getInvestorId()),
                        w.getInvestorName(),
                        String.valueOf(w.getProductId()),
                        w.getProductName(),
                        w.getProductType(),
                        w.getAmount().toPlainString(),
                        w.getBalanceAfterWithdrawal().toPlainString(),
                        w.getCreatedAt().format(FORMATTER),
                        w.getStatus().name()
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV: " + e.getMessage());
        }
        return sw.toString().getBytes();
    }
}