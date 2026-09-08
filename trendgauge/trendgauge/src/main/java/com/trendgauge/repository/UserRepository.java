package com.trendgauge.repository;

import com.trendgauge.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
Optional<UserEntity> findByStoreIdAndRole(Long storeId, String role);

}
