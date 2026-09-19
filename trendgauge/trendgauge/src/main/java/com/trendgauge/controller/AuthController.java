package com.trendgauge.controller;

import com.trendgauge.model.request.AdminLoginInput;
import com.trendgauge.service.AdminLoginService;
import com.trendgauge.service.ManagerPinService;
import com.trendgauge.service.StoreLoginService;
import com.trendgauge.model.request.StoreLoginInput;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final StoreLoginService storeLoginService;
    public final AdminLoginService adminLoginService;
    private final ManagerPinService managerPinService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(StoreLoginService storeLoginService, AdminLoginService adminLoginService, ManagerPinService managerPinService) {
        this.storeLoginService = storeLoginService;
        this.adminLoginService = adminLoginService;
        this.managerPinService = managerPinService;
    }

    //    店舗ログイン
    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("storeLoginInput")) {
            model.addAttribute("storeLoginInput", new StoreLoginInput());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Validated @ModelAttribute("storeLoginInput") StoreLoginInput storeLoginInput,
            BindingResult br,
            RedirectAttributes ra,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.storeLoginInput", br);
            ra.addFlashAttribute("storeLoginInput", storeLoginInput);
            return "redirect:/login";
        }

        boolean result = storeLoginService.login(storeLoginInput.getStoreCode(), storeLoginInput.getPassword());
        if (result) {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            storeLoginInput.getStoreCode(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_STORE_TERMINAL"))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            securityContextRepository.saveContext(
                    SecurityContextHolder.getContext(),
                    request,
                    response
            );
            return "redirect:/main";
        } else {
            ra.addFlashAttribute("errorMessage", "店舗コードまたはパスワードが正しくありません");
            return "redirect:/login";
        }

    }

    //    管理者ログイン
    @GetMapping("/admin/login")
    public String adminLoginPage(Model model) {
        if (!model.containsAttribute("adminLoginInput")) {
            model.addAttribute("adminLoginInput", new AdminLoginInput());
        }
        return "auth/admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @Validated @ModelAttribute("adminLoginInput") AdminLoginInput adminLoginInput,
            BindingResult br,
            RedirectAttributes ra,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.adminLoginInput", br);
            ra.addFlashAttribute("adminLoginInput", adminLoginInput);
            return "redirect:/admin/login";
        }

        boolean result = adminLoginService.adminLogin(adminLoginInput.getEmail(), adminLoginInput.getPassword());
        if (result) {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            adminLoginInput.getEmail(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            securityContextRepository.saveContext(
                    SecurityContextHolder.getContext(),
                    request,
                    response
            );
            return "redirect:/admin/main";
        } else {
            ra.addFlashAttribute("errorMessage", "メールアドレスまたはパスワードが正しくありません");
            return "redirect:/admin/login";
        }
    }

    //    店長ログイン
    @PostMapping("/manager/login")
    @ResponseBody
    public boolean managerLogin(
            Authentication authentication,
            @RequestParam("pin") String pin,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String storeCode = authentication.getName();
        boolean result = managerPinService.managerLogin(storeCode, pin);
        if (!result) {
            return false;
        }
        Authentication newAuthentication =
                new UsernamePasswordAuthenticationToken(
                        storeCode,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_STORE_MANAGER"))
                );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        securityContextRepository.saveContext(
                SecurityContextHolder.getContext(),
                request,
                response
        );
        return true;
    }

    @PostMapping("/manager/logout")
    public String managerLogout(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String storeCode = authentication.getName();
        Authentication newAuthentication =
                new UsernamePasswordAuthenticationToken(
                        storeCode,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_STORE_TERMINAL"))
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        securityContextRepository.saveContext(
                SecurityContextHolder.getContext(),
                request,
                response
        );
        return "redirect:/main";
    }
}
