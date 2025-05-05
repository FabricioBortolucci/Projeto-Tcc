package com.produto.oficina.controller;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.Compra;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.CompraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/caixa")
public class CaixaController {

    private final CaixaService caixaService;

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    @GetMapping
    public String caixaList(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {
        Page<Caixa> caixaPage = caixaService.findAll(PageRequest.of(page, size));
        model.addAttribute("caixaPage", caixaPage);
        model.addAttribute("currentPage", page);
        return "caixa/caixaList";
    }

    @GetMapping("/cadastro")
    public String caixaForm(Model model) {
        model.addAttribute("caixa", new Caixa());
        return "caixa/caixaForm";
    }

    @PostMapping("/cadastrar")
    public String caixaSave(@ModelAttribute Compra compra) {
        return "redirect:/caixa";
    }

    @PostMapping("/remover/{index}")
    public String caixaDelete(@PathVariable Long index) {
        return "redirect:/caixa";
    }

    @GetMapping("/editar/{index}")
    public String caixaEdit(@PathVariable Long index, Model model) {
        return "caixa/caixaForm";
    }

}
