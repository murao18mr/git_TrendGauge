package com.trendgauge.service;

import com.trendgauge.model.entity.IntegrationEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.repository.IntegrationRepository;
import com.trendgauge.repository.StoreRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PosSyncScheduler {

    private final IntegrationRepository integrationRepository;
    private final StoreRepository storeRepository;
    private final AdminPosSettingService adminPosSettingService;

    public PosSyncScheduler(IntegrationRepository integrationRepository, StoreRepository storeRepository, AdminPosSettingService adminPosSettingService) {
        this.integrationRepository = integrationRepository;
        this.storeRepository = storeRepository;
        this.adminPosSettingService = adminPosSettingService;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Tokyo")
    public void syncSales() {

        String currentTime = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        List<IntegrationEntity> integrations = integrationRepository.findByAutoSyncTrue();

        for (IntegrationEntity integration : integrations) {

            if (!currentTime.equals(integration.getSyncTime())) {
                continue;
            }

            try {
                Long storeId = integration.getStoreId();

                StoreEntity store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new IllegalArgumentException("対象店舗が見つかりません"));

                Long companyId = store.getCompanyId();

                String result = adminPosSettingService.syncSalesAutomatically(storeId, companyId);

                System.out.println(result);

            } catch (Exception e) {
                System.err.println("POS自動同期に失敗しました。店舗ID：" + integration.getStoreId());
                e.printStackTrace();
            }
        }
    }
}