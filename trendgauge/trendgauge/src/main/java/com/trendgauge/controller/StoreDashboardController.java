package com.trendgauge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StoreDashboardController {

    @GetMapping("/main")
    public String storeDashboardPage(){
        return "dashboard/store";
    }


}
