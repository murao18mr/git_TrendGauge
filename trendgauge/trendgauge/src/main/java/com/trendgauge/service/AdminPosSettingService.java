package com.trendgauge.service;

import com.trendgauge.model.entity.IntegrationEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.request.AdminPosSettingInput;
import com.trendgauge.model.response.AdminPosSettingResponse;
import com.trendgauge.repository.IntegrationRepository;
import com.trendgauge.repository.StoreRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminPosSettingService {
    private final IntegrationRepository integrationRepository;
    private final StoreRepository storeRepository;
    private final PosEncryptionService posEncryptionService;
    private final SmaregiApiService smaregiApiService;
    private final SaleService saleService;

    public AdminPosSettingService(IntegrationRepository integrationRepository, StoreRepository storeRepository, PosEncryptionService posEncryptionService, SmaregiApiService smaregiApiService, SaleService saleService){
        this.integrationRepository = integrationRepository;
        this.storeRepository = storeRepository;
        this.posEncryptionService = posEncryptionService;
        this.smaregiApiService = smaregiApiService;
        this.saleService = saleService;
    }

    private StoreEntity getTargetStore(Long storeId, Long companyId) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("対象店舗が見つかりません"));

        if (!store.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("この店舗を操作する権限がありません");
        }

        return store;
    }

    public AdminPosSettingResponse getSetting(Long storeId, Long companyId) {
        getTargetStore(storeId, companyId);

        Optional<IntegrationEntity> optionalIntegration = integrationRepository.findByStoreId(storeId);

        if (optionalIntegration.isPresent()) {
            IntegrationEntity integration = optionalIntegration.get();

            AdminPosSettingResponse response = new AdminPosSettingResponse(
                    true,
                    integration.getPosType(),
                    integration.getContractId(),
                    integration.getClientId(),
                    integration.getSmaregiStoreId(),
                    integration.isAutoSync(),
                    integration.getSyncTime()
            );

            return response;
        }

        AdminPosSettingResponse response = new AdminPosSettingResponse(
                false,
                null,
                null,
                null,
                null,
                false,
                "02:00"
        );

        return response;
    }

    public void saveSetting(AdminPosSettingInput input, Long companyId) throws Exception {
        if (input.getStoreId() == null) {
            throw new IllegalArgumentException("対象店舗を選択してください");
        }

        if (!"smaregi".equals(input.getPosType())) {
            throw new IllegalArgumentException("対応していないPOSレジです");
        }

        if (input.getContractId() == null || input.getContractId().isBlank()
                || input.getClientId() == null || input.getClientId().isBlank()) {
            throw new IllegalArgumentException("API接続情報を入力してください");
        }

        if (input.isAutoSync()
                && (input.getSyncTime() == null
                || !input.getSyncTime().matches("([01]\\d|2[0-3]):[0-5]\\d"))) {
            throw new IllegalArgumentException("同期時刻を正しく入力してください");
        }

        getTargetStore(input.getStoreId(), companyId);

        IntegrationEntity integration = integrationRepository.findByStoreId(input.getStoreId())
                .orElseGet(IntegrationEntity::new);

        boolean isNew = integration.getId() == null;
        boolean hasSecret = input.getClientSecret() != null
                && !input.getClientSecret().isBlank();

        if (isNew && !hasSecret) {
            throw new IllegalArgumentException("クライアントシークレットは必須入力です");
        }

        integration.setStoreId(input.getStoreId());
        integration.setPosType(input.getPosType());
        integration.setContractId(input.getContractId());
        integration.setClientId(input.getClientId());
        if (hasSecret) {
            integration.setClientSecret(posEncryptionService.encrypt(input.getClientSecret()));
        }
        integration.setSmaregiStoreId(input.getSmaregiStoreId());
        integration.setAutoSync(input.isAutoSync());
        integration.setSyncTime(
                input.isAutoSync() ? input.getSyncTime() : "02:00"
        );
        if (integration.getId() == null) {
            integration.setCreatedAt(LocalDateTime.now());
        }

        integration.setUpdatedAt(LocalDateTime.now());

        integrationRepository.save(integration);
    }


    public String getTestSecret(AdminPosSettingInput input, Long companyId) throws Exception {

        getTargetStore(input.getStoreId(), companyId);

        if (input.getClientSecret() != null && !input.getClientSecret().isBlank()) {
            return input.getClientSecret();
        }

        IntegrationEntity integration = integrationRepository.findByStoreId(input.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "クライアントシークレットを入力してください"));

        return posEncryptionService.decrypt(integration.getClientSecret());
    }

    public String syncSalesAutomatically(Long storeId, Long companyId) throws Exception {

        LocalDate saleDate = LocalDate.now().minusDays(1);

        return syncSales(storeId, companyId, saleDate);
    }

    public String syncSales(Long storeId, Long companyId) throws Exception {

        LocalDate saleDate = LocalDate.now();

        return syncSales(storeId, companyId, saleDate);
    }

    private String syncSales(Long storeId, Long companyId, LocalDate saleDate) throws Exception {

        getTargetStore(storeId, companyId);

        IntegrationEntity integration = integrationRepository.findByStoreId(storeId)
                .orElseThrow(() -> new IllegalArgumentException("POS連携設定がありません"));

        if (!"smaregi".equals(integration.getPosType())) {
            throw new IllegalArgumentException("対応していないPOSレジです");
        }

        if (integration.getSmaregiStoreId() == null
                || integration.getSmaregiStoreId().isBlank()) {
            throw new IllegalArgumentException("スマレジ店舗IDが未設定です");
        }

        String clientSecret = posEncryptionService.decrypt(
                integration.getClientSecret()
        );

        JsonNode transactions = smaregiApiService.getTransactions(
                integration.getContractId(),
                integration.getClientId(),
                clientSecret,
                integration.getSmaregiStoreId(),
                saleDate
        );

        if (transactions == null || !transactions.isArray()) {
            throw new IllegalStateException("取引データを取得できませんでした");
        }

        int count = transactions.size();
        long totalAmount = 0;

        for (JsonNode transaction : transactions) {

            String transactionDivision =
                    transaction.path("transactionHeadDivision").asText();

            String cancelDivision =
                    transaction.path("cancelDivision").asText();

            String returnSales =
                    transaction.path("returnSales").asText();

            if (!"1".equals(transactionDivision)) {
                throw new IllegalStateException("未対応の取引区分です");
            }

            if ("1".equals(cancelDivision)) {
                continue;
            }

            if (!"0".equals(cancelDivision)) {
                throw new IllegalStateException("未対応の取消区分です");
            }

            if (!"0".equals(returnSales) && !"1".equals(returnSales)) {
                throw new IllegalStateException("未対応の返品区分です");
            }

            String amountText = transaction.path("total").asText();

            long amount = Long.parseLong(amountText);

            totalAmount += amount;
        }

        saleService.savePosSales(storeId, saleDate, totalAmount);

        return "売上同期が完了しました（" + saleDate
                + "：取得" + count + "件、売上" + totalAmount + "円）";
    }


}
