package com.produto.oficina.controller;

import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.ProdutoTipo;
import com.produto.oficina.service.PlanoDeContasService;
import com.produto.oficina.service.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final PlanoDeContasService planoDeContasService;

    public ProdutoController(ProdutoService produtoService, PlanoDeContasService planoDeContasService) {
        this.produtoService = produtoService;
        this.planoDeContasService = planoDeContasService;
    }

    private final List<Pessoa> fornecedores = new ArrayList<>();

    @GetMapping
    public String produtoList(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {
        Page<Produto> produtoPage = produtoService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("produtoPage", produtoPage);
        model.addAttribute("currentPage", page);
        return "produto/prodList";
    }

    @GetMapping("/cadastro")
    public String produtoForm(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("tipo_lista", ProdutoTipo.values());
        model.addAttribute("contasEstoque", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.ATIVO));
        model.addAttribute("contasCusto", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.CUSTO));
        model.addAttribute("contasReceita", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.RECEITA));
        return "produto/prodForm";
    }

    @PostMapping("/cadastrar")
    public String produtoSave(@ModelAttribute Produto produto, RedirectAttributes redirectAttributes) {
        produtoService.save(produto);
        redirectAttributes.addFlashAttribute("produto_cadastrado", true);
        return "redirect:/produto";
    }

    @PostMapping("/remover/{index}")
    public String produtoDelete(@PathVariable Long index) {
        produtoService.deleteProd(index);
        return "redirect:/produto";
    }

    @GetMapping("/editar/{index}")
    public String produtoEdit(@PathVariable Long index, Model model) {
        Produto prod = produtoService.findById(index);

        model.addAttribute("produto", prod);
        model.addAttribute("tipo_lista", ProdutoTipo.values());
        model.addAttribute("contasEstoque", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.ATIVO));
        model.addAttribute("contasCusto", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.CUSTO));
        model.addAttribute("contasReceita", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.RECEITA));
        return "produto/prodForm";
    }

    @GetMapping("/visualizar/{index}")
    public String visualizarProduto(@PathVariable Long index, Model model) {
        model.addAttribute("produto", produtoService.findById(index));
        return "produto/prodVisu";
    }
}
