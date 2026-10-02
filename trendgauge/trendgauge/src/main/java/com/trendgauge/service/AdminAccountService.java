package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.request.AdminAccountInput;
import com.trendgauge.model.response.StoreAccountResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminAccountService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountService(StoreRepository storeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StoreAccountResponse> getStoreAccounts(Long companyId) {
        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);
        List<StoreAccountResponse> result = new ArrayList<>();

        for (StoreEntity store : stores) {
            Optional<UserEntity> user = userRepository.findByStoreIdAndRole(store.getId(), "store_terminal");

            result.add(new StoreAccountResponse(
                    store.getId(),
                    store.getStoreCode(),
                    store.getStoreName(),
                    user.map(UserEntity::getEmail).orElse(""),
                    store.getStatus()
            ));
        }

        return result;
    }

    public boolean isStoreCodeUsed(String storeCode) {
        return storeRepository.findByStoreCode(storeCode).isPresent();
    }

    @Transactional
    public void registerAccount(AdminAccountInput input, Long companyId) {

        if (storeRepository.findByStoreCode(input.getStoreCode()).isPresent()) {
            throw new IllegalArgumentException("店舗コードはすでに使用されています");
        }

        LocalDateTime now = LocalDateTime.now();

        StoreEntity store = new StoreEntity();
        store.setCompanyId(companyId);
        store.setStoreName(input.getStoreName());
        store.setStoreCode(input.getStoreCode());
        store.setStatus("active");
        store.setManagerPin(passwordEncoder.encode(input.getManagerPin()));
        store.setOpeningTime(input.getOpeningTime());
        store.setClosingTime(input.getClosingTime());
        store.setCreatedAt(now);
        store.setUpdatedAt(now);

        StoreEntity savedStore = storeRepository.save(store);

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole("store_terminal");
        user.setStoreId(savedStore.getId());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);
    }
}
