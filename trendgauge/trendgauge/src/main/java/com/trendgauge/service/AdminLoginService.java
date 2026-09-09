package com.trendgauge.service;

import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean adminLogin(String email, String password){
        Optional<UserEntity> user = userRepository.findByEmailAndRole(email, "admin");
        if(user.isEmpty()){
            return false;
        }
        UserEntity userEntity = user.get();
        String encodedPassword = userEntity.getPassword();

        boolean passwordMatches = passwordEncoder.matches(password, encodedPassword);
        return passwordMatches;
    }
}
