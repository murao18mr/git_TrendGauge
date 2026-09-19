package com.trendgauge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/menu")
    public String ManagerMenuPage(){
        return"/manager/menu";
    }
}
