package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreLoginService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreLoginService(StoreRepository storeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean login(String storeCode, String password) {
        Optional<StoreEntity> store = storeRepository.findByStoreCode(storeCode);

        if (store.isEmpty()) {
            return false;
        }
        StoreEntity storeEntity = store.get();
        Long storeId = storeEntity.getId();

        Optional<UserEntity> user = userRepository.findByStoreIdAndRole(storeId, "store_terminal");

        if (user.isEmpty()) {
            return false;
        }
        UserEntity userEntity = user.get();
        String encodedPassword = userEntity.getPassword();

        boolean passwordMatches = passwordEncoder.matches(password, encodedPassword);
        return passwordMatches;
    }


}
