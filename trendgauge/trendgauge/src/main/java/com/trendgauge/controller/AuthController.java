package com.trendgauge.controller;

import com.trendgauge.Service.StoreLoginService;
import com.trendgauge.model.request.LoginInput;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final StoreLoginService storeLoginService;

    public AuthController(StoreLoginService storeLoginService){
        this.storeLoginService = storeLoginService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute("loginInput")LoginInput loginInput,
            BindingResult br,
            RedirectAttributes ra
    ){
        if(br.hasErrors()){
            ra.addFlashAttribute("org.springframework.validation.BindingResult.loginInput", br);
            ra.addFlashAttribute("loginInput", loginInput);
            return "redirect:/login";
        }

        boolean result = storeLoginService.login(loginInput.getStoreCode(), loginInput.getPassword());
        if (result){
            return "/main";
        } else {
            ra.addFlashAttribute("errorMessage", "店舗コードまたはパスワードが正しくありません");
            return "redirect:/login";
        }

    }

}
