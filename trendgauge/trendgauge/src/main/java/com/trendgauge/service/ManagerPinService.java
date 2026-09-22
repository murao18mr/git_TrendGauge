package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.repository.StoreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerPinService {
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerPinService(StoreRepository storeRepository, PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean managerLogin(String storeCode, String pin) {
        StoreEntity store = storeRepository.findByStoreCode(storeCode).orElseThrow();
        String encodedPin = store.getManagerPin();
        return passwordEncoder.matches(pin, encodedPin);
    }

    public boolean changePin(String storeCode, String currentPin, String newPin) {
        StoreEntity store = storeRepository.findByStoreCode(storeCode).orElseThrow();

        if (!passwordEncoder.matches(currentPin, store.getManagerPin())) {
            return false;
        }

        store.setManagerPin(passwordEncoder.encode(newPin));
        storeRepository.save(store);
        return true;
    }
}
