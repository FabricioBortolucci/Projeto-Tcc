package com.produto.oficina.controller;

import com.produto.oficina.model.Compra;
import com.produto.oficina.model.Produto;
import com.produto.oficina.service.CompraService;
import com.produto.oficina.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/compra")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public String compraList(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {
        Page<Compra> compraPage = compraService.findAll(PageRequest.of(page, size));
        model.addAttribute("compraPage", compraPage);
        model.addAttribute("currentPage", page);
        return "compra/compraList";
    }

    @GetMapping("/cadastro")
    public String compraForm(Model model) {
        model.addAttribute("compra", new Compra());
        return "compra/compraForm";
    }

    @PostMapping("/cadastrar")
    public String compraSave(@ModelAttribute Compra compra) {
        compraService.save(compra);
        return "redirect:/compra";
    }

    @PostMapping("/remover/{index}")
    public String compraDelete(@PathVariable Long index) {
        compraService.deleteProd(index);
        return "redirect:/compra";
    }

    @GetMapping("/editar/{index}")
    public String compraEdit(@PathVariable Long index, Model model) {
        model.addAttribute("compra", compraService.findById(index));
        return "compra/compraForm";
    }

}
