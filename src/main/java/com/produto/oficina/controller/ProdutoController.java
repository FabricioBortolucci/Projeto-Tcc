package com.produto.oficina.controller;

import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.enums.ProdutoTipo;
import com.produto.oficina.service.PessoaService;
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
    private final PessoaService pessoaService;

    public ProdutoController(PessoaService pessoaService, ProdutoService produtoService) {
        this.pessoaService = pessoaService;
        this.produtoService = produtoService;
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
        fornecedores.clear();
        model.addAttribute("produto", new Produto());
        model.addAttribute("fornecedores", fornecedores);
        model.addAttribute("tipo_lista", ProdutoTipo.values());
        model.addAttribute("forn_list", produtoService.buscaFornecedores());
        return "produto/prodForm";
    }

    @PostMapping("/cadastrar")
    public String produtoSave(@ModelAttribute Produto produto, RedirectAttributes redirectAttributes) {
        produto.getFornecedores().addAll(fornecedores);
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
        fornecedores.clear();
        fornecedores.addAll(prod.getFornecedores());

        model.addAttribute("produto", prod);
        model.addAttribute("fornecedores", fornecedores);
        model.addAttribute("tipo_lista", ProdutoTipo.values());
        model.addAttribute("forn_list", produtoService.buscaFornecedores());
        return "produto/prodForm";
    }

    @PostMapping("/cadastro/adicionar")
    public String adicionarForn(@RequestParam(value = "fornecedor", required = false) Long fornecedor, Model model) {
        if (fornecedor != null) {
            pessoaService.buscaFornecedorPorId(fornecedor)
                    .ifPresent(pessoa -> {
                        if (!fornecedores.contains(pessoa)) {
                            fornecedores.add(pessoa);
                        }
                    });
        }
        model.addAttribute("fornecedores", fornecedores);
        return "fragments/produtoFrags/fornecedorForm :: fornecedorTable";
    }

    @DeleteMapping("/cadastro/remover/{index}")
    public String removerForn(@PathVariable Integer index, Model model) {
        if (index >= 0 && index < fornecedores.size()) {
            fornecedores.remove(index.intValue());
        }
        model.addAttribute("fornecedores", fornecedores);
        return "fragments/produtoFrags/fornecedorForm :: fornecedorTable";
    }

    @GetMapping("/visualizar/{index}")
    public String visualizarProduto(@PathVariable Long index, Model model) {
        model.addAttribute("produto", produtoService.findById(index));
        return "produto/prodVisu";
    }
}
