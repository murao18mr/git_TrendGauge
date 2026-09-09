package com.trendgauge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    @GetMapping("/admin/main")
    public String adminDashboardPage(){
        return "dashboard/admin";
    }
}
