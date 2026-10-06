package com.trendgauge.controller;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.UserEntity;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/setting")
public class AdminPosSettingController {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public AdminPosSettingController(UserRepository userRepository, StoreRepository storeRepository){
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @GetMapping("/pos")
    public String posSettingPage(Authentication authentication, Model model) {
        String email = authentication.getName();
        UserEntity admin = userRepository.findByEmailAndRole(email, "admin").orElseThrow();
        Long companyId = admin.getCompanyId();

        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        model.addAttribute("stores", stores);
        return "/admin/pos-setting";
    }
}
