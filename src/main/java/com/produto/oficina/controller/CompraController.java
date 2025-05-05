package com.produto.oficina.controller;

import com.produto.oficina.model.Compra;
import com.produto.oficina.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/compra")
public class CompraController {

    private final CompraService compraService;
    private final ProdutoService produtoService;
    private final PessoaService pessoaService;
    private final CaixaService caixaService;
    private final MateriaPrimaService materiaPrimaService;

    public CompraController(CompraService compraService, ProdutoService produtoService, PessoaService pessoaService, CaixaService caixaService, MateriaPrimaService materiaPrimaService) {
        this.compraService = compraService;
        this.produtoService = produtoService;
        this.pessoaService = pessoaService;
        this.caixaService = caixaService;
        this.materiaPrimaService = materiaPrimaService;
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
       /* if (caixaService.verificaCaixaAberto()) {
            return "redirect:/compra";
        }*/
        model.addAttribute("compra", new Compra());
        model.addAttribute("fornecedores_compra", pessoaService.buscaFornecedores());
        return "compra/compraForm";
    }

    @GetMapping("/buscarProdutos")
    public String buscarProdutos(@RequestParam(required = false) Long fornecedorId,
                                 Model model) {
        model.addAttribute("produtos", produtoService.buscarProdutosPorFornecedor(fornecedorId));
        model.addAttribute("materias_primas", materiaPrimaService.buscarMpsPorFornecedor(fornecedorId));
        return "fragments/compraFrags/produtosSelect :: produtoSelect";
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
