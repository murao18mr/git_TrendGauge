package com.trendgauge.service;

import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.response.AdminExportResponse;
import com.trendgauge.model.response.AdminMonthlyExportResponse;
import com.trendgauge.repository.SaleRepository;
import com.trendgauge.repository.StoreRepository;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminExportService {
    private final StoreRepository storeRepository;
    private final SaleRepository saleRepository;

    public AdminExportService(StoreRepository storeRepository, SaleRepository saleRepository) {
        this.storeRepository = storeRepository;
        this.saleRepository = saleRepository;
    }

    public List<StoreEntity> getTargetStores(Long companyId, Long storeId) {
        if (storeId == null) {
            return storeRepository.findByCompanyId(companyId);
        }

        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        for (StoreEntity store : stores) {
            if (store.getId().equals(storeId)) {
                return List.of(store);
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    public List<SaleEntity> getDailySales(Long storeId, LocalDate startDate, LocalDate endDate) {
        return saleRepository.findByStoreIdAndSaleDateBetweenOrderBySaleDateAsc(storeId, startDate, endDate);
    }

    public List<AdminExportResponse> createDailyData(Long companyId, Long storeId, LocalDate startDate, LocalDate endDate) {
        List<StoreEntity> stores = getTargetStores(companyId, storeId);
        List<AdminExportResponse> result = new ArrayList<>();

        for (StoreEntity store : stores) {
            List<SaleEntity> sales = getDailySales(store.getId(), startDate, endDate);

            for (SaleEntity sale : sales) {
                result.add(new AdminExportResponse(
                        store.getStoreName(),
                        sale.getSaleDate(),
                        sale.getAmount(),
                        sale.getCustomerCount()
                ));
            }
        }

        return result;
    }

    public byte[] createDailyCsv(List<AdminExportResponse> data) {
        StringBuilder csv = new StringBuilder();

        csv.append("店舗名,日付,売上,客数\n");

        for (AdminExportResponse row : data) {
            csv.append(row.getStoreName()).append(",");
            csv.append(row.getSaleDate()).append(",");
            csv.append(row.getAmount() != null ? row.getAmount() : "").append(",");
            csv.append(row.getCustomerCount() != null ? row.getCustomerCount() : "").append("\n");
        }

        return ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
    }

    public List<AdminMonthlyExportResponse> createMonthlyData(Long companyId, Long storeId, String targetMonth) {
        List<StoreEntity> stores = getTargetStores(companyId, storeId);
        List<AdminMonthlyExportResponse> result = new ArrayList<>();

        YearMonth month = YearMonth.parse(targetMonth);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        for (StoreEntity store : stores) {
            List<SaleEntity> sales = getDailySales(store.getId(), startDate, endDate);

            long totalSales = 0L;
            int totalCustomers = 0;

            for (SaleEntity sale : sales) {
                if (sale.getAmount() != null) {
                    totalSales += sale.getAmount();
                }
                if (sale.getCustomerCount() != null) {
                    totalCustomers += sale.getCustomerCount();
                }
            }

            result.add(new AdminMonthlyExportResponse(
                    store.getStoreName(),
                    targetMonth,
                    totalSales,
                    totalCustomers
            ));
        }

        return result;
    }

    public byte[] createMonthlyCsv(List<AdminMonthlyExportResponse> data) {
        StringBuilder csv = new StringBuilder();

        csv.append("店舗名,対象月,売上合計,客数合計\n");

        for (AdminMonthlyExportResponse row : data) {
            csv.append(row.getStoreName()).append(",");
            csv.append(row.getTargetMonth()).append(",");
            csv.append(row.getTotalAmount() != null ? row.getTotalAmount() : "").append(",");
            csv.append(row.getTotalCustomerCount() != null ? row.getTotalCustomerCount() : "").append("\n");
        }

        return ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
    }

    public byte[] createDailyPdf(List<AdminExportResponse> data, LocalDate startDate, LocalDate endDate, String storeName) {
        try {
            ClassPathResource regularResource = new ClassPathResource("fonts/BIZUDGothic-Regular.ttf");
            byte[] regularBytes = regularResource.getInputStream().readAllBytes();
            BaseFont regularFont = BaseFont.createFont("BIZUDGothic-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.CACHED, regularBytes, null);

            ClassPathResource boldResource = new ClassPathResource("fonts/BIZUDGothic-Bold.ttf");
            byte[] boldBytes = boldResource.getInputStream().readAllBytes();
            BaseFont boldFont = BaseFont.createFont("BIZUDGothic-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.CACHED, boldBytes, null);

            Font titleFont = new Font(boldFont, 18);
            Font headerFont = new Font(boldFont, 11);
            Font bodyFont = new Font(regularFont, 10.5f);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            document.add(new Paragraph("TrendGauge 日次売上レポート", titleFont));
            document.add(new Paragraph("対象期間：" + startDate + " ～ " + endDate, bodyFont));
            document.add(new Paragraph("対象店舗：" + storeName, bodyFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            Color headerColor = new Color(245, 245, 245);

            PdfPCell storeHeader = new PdfPCell(new Phrase("店舗名", headerFont));
            storeHeader.setBackgroundColor(headerColor);
            table.addCell(storeHeader);

            PdfPCell dateHeader = new PdfPCell(new Phrase("日付", headerFont));
            dateHeader.setBackgroundColor(headerColor);
            table.addCell(dateHeader);

            PdfPCell amountHeader = new PdfPCell(new Phrase("売上", headerFont));
            amountHeader.setBackgroundColor(headerColor);
            table.addCell(amountHeader);

            PdfPCell customerHeader = new PdfPCell(new Phrase("客数", headerFont));
            customerHeader.setBackgroundColor(headerColor);
            table.addCell(customerHeader);

            for (AdminExportResponse row : data) {
                table.addCell(new PdfPCell(new Phrase(row.getStoreName(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getSaleDate().toString(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getAmount() != null ? row.getAmount().toString() : "", bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getCustomerCount() != null ? row.getCustomerCount().toString() : "", bodyFont)));
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("日次PDF作成に失敗しました", e);
        }
    }

    public byte[] createMonthlyPdf(List<AdminMonthlyExportResponse> data, String targetMonth, String storeName) {
        try {
            ClassPathResource regularResource = new ClassPathResource("fonts/BIZUDGothic-Regular.ttf");
            byte[] regularBytes = regularResource.getInputStream().readAllBytes();
            BaseFont regularFont = BaseFont.createFont("BIZUDGothic-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.CACHED, regularBytes, null);

            ClassPathResource boldResource = new ClassPathResource("fonts/BIZUDGothic-Bold.ttf");
            byte[] boldBytes = boldResource.getInputStream().readAllBytes();
            BaseFont boldFont = BaseFont.createFont("BIZUDGothic-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.CACHED, boldBytes, null);

            Font titleFont = new Font(boldFont, 18);
            Font headerFont = new Font(boldFont, 11);
            Font bodyFont = new Font(regularFont, 10.5f);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            document.add(new Paragraph("TrendGauge 月次売上レポート", titleFont));
            document.add(new Paragraph("対象月：" + targetMonth, bodyFont));
            document.add(new Paragraph("対象店舗：" + storeName, bodyFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            Color headerColor = new Color(245, 245, 245);

            PdfPCell storeHeader = new PdfPCell(new Phrase("店舗名", headerFont));
            storeHeader.setBackgroundColor(headerColor);
            table.addCell(storeHeader);

            PdfPCell monthHeader = new PdfPCell(new Phrase("対象月", headerFont));
            monthHeader.setBackgroundColor(headerColor);
            table.addCell(monthHeader);

            PdfPCell amountHeader = new PdfPCell(new Phrase("売上合計", headerFont));
            amountHeader.setBackgroundColor(headerColor);
            table.addCell(amountHeader);

            PdfPCell customerHeader = new PdfPCell(new Phrase("客数合計", headerFont));
            customerHeader.setBackgroundColor(headerColor);
            table.addCell(customerHeader);

            for (AdminMonthlyExportResponse row : data) {
                table.addCell(new PdfPCell(new Phrase(row.getStoreName(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getTargetMonth(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getTotalAmount() != null ? row.getTotalAmount().toString() : "", bodyFont)));
                table.addCell(new PdfPCell(new Phrase(row.getTotalCustomerCount() != null ? row.getTotalCustomerCount().toString() : "", bodyFont)));
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("月次PDF作成に失敗しました", e);
        }
    }
}
