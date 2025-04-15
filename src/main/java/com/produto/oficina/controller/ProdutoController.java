package com.produto.oficina.controller;

import com.produto.oficina.model.Produto;
import com.produto.oficina.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String produtoList(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "produto/prodList";
    }

    @GetMapping("/cadastro")
    public String produtoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/prodForm";
    }

    @PostMapping("/cadastrar")
    public String produtoSave(@ModelAttribute Produto produto) {
        produtoService.save(produto);
        return "redirect:/produto";
    }

    @PostMapping("/remover/{index}")
    public String produtoDelete(@PathVariable Long index) {
        produtoService.deleteProd(index);
        return "redirect:/produto";
    }


}
