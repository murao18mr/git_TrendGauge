package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.model.response.StoreAccountResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminAccountService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public AdminAccountService(StoreRepository storeRepository, UserRepository userRepository){
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
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
}
