package com.produto.oficina.controller;

import com.produto.oficina.dto.DashboardPageDTO;
import com.produto.oficina.service.DashBoardService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthController {

    private final DashBoardService dashBoardService;

    public AuthController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "home";
        }
        return "login";
    }



    @GetMapping({ "/home"})
    public String homePage(Model model) {
        DashboardPageDTO dashboardData = dashBoardService.getDashboardPageData();

        // O HTML espera "estatisticas" e "atividadesRecentes"
        // O segundo agora conterá os alertas de estoque.
        model.addAttribute("estatisticas", dashboardData.estatisticas());
        model.addAttribute("atividadesRecentes", dashboardData.atividadesRecentes()); // Esta lista agora são os alertas

        return "home";
    }

    @GetMapping("/side")
    public String side(Model model) {
        return "fragments/sidebar";
    }

}
