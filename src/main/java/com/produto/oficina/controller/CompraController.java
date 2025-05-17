package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.dto.CompraDTO;
import com.produto.oficina.model.Compra;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.CompraService;
import com.produto.oficina.service.PessoaService;
import com.produto.oficina.service.ProdutoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/compra")
@SessionAttributes("compra")
public class CompraController {

    private final CompraService compraService;
    private final ProdutoService produtoService;
    private final PessoaService pessoaService;
    private final CaixaService caixaService;

    public CompraController(CompraService compraService, ProdutoService produtoService, PessoaService pessoaService, CaixaService caixaService) {
        this.compraService = compraService;
        this.produtoService = produtoService;
        this.pessoaService = pessoaService;
        this.caixaService = caixaService;
    }

    @ModelAttribute("compra")
    public CompraDTO compraDTO() {
        return new CompraDTO();
    }

    @GetMapping
    public String compraList(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {
        Page<Compra> compraPage = compraService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("compraPage", compraPage);
        model.addAttribute("currentPage", page);
        return "compra/compraList";
    }

    @GetMapping("/cadastro")
    public String compraForm(RedirectAttributes redirectAttributes, Model model) {
        if (!caixaService.verificaCaixaAberto()) {
            redirectAttributes.addFlashAttribute("mostrarModal", true);
            return "redirect:/compra";
        }
        model.addAttribute("compra", new CompraDTO());
        model.addAttribute("planos_pagamento", PlanoPagamento.values());
        model.addAttribute("fornecedores_compra", pessoaService.buscaFornecedores());
        model.addAttribute("produtos", produtoService.findAll());
        return "compra/compraForm";
    }

    @PostMapping("/cadastrar")
    public String compraSave(@ModelAttribute("compra") CompraDTO compraDTO,
                             RedirectAttributes redirectAttributes) {
        compraService.save(compraDTO);
        redirectAttributes.addFlashAttribute("compra_cadastrada", true);
        return "redirect:/compra";
    }

    @PostMapping("/salvar-sem-finalizar")
    public ResponseEntity<Void> compraSaveSemFinalizar(@ModelAttribute("compra") CompraDTO compraDTO,
                                                       RedirectAttributes redirectAttributes) {
        compraService.salvarCompraAberta(compraDTO);
        redirectAttributes.addFlashAttribute("compra_cadastrada", true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("HX-Redirect", "/compra");
        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER); // Redirect com HTMX
    }

    @GetMapping("/visualizar/{index}")
    public String visualizarCompra(@PathVariable Long index, Model model) {
        model.addAttribute("compra", compraService.findById(index));
        return "compra/compraVisu";
    }


    @GetMapping("/produto/buscar-precoProd")
    public String buscarPrecoProduto(@ModelAttribute("compra") CompraDTO compraDTO,
                                     @RequestParam("prodId") Long produtoId,
                                     Model model) {
        Produto produto = produtoService.findById(produtoId);
        compraDTO.setValorUnitarioItens(produto.getPrecoCusto());

        model.addAttribute("compra", compraDTO);
        return "fragments/compraFrags/compraReplaces :: detalhesItem";
    }

    @PostMapping("/cadastro/adicionar-item")
    public String adicionarItem(@ModelAttribute("compra") CompraDTO compraDTO,
                                @RequestParam("prodId") Long produtoId,
                                @RequestParam("quantItens") Integer quantidade,
                                @RequestParam("valorUnitarioItens") BigDecimal valorCusto,
                                Model model) {
        compraService.adicionarItemCompra(compraDTO, produtoId, quantidade, valorCusto);
        model.addAttribute("compra", compraDTO);
        return "fragments/compraFrags/compraReplaces :: itensCompraTable";
    }

    @DeleteMapping("/cadastro/remover-item/{index}")
    public String removerItem(@PathVariable("index") int index,
                              @ModelAttribute("compra") CompraDTO compraDTO,
                              Model model) {
        if (compraDTO.getItemCompraList() != null && index >= 0 && index < compraDTO.getItemCompraList().size()) {
            compraDTO.getItemCompraList().remove(index);
        }
        model.addAttribute("compra", compraDTO);
        return "fragments/compraFrags/compraReplaces :: itensCompraTable";
    }

    @GetMapping("/cadastro/buscar-planoPag")
    public String buscarPlanoPagamento(@ModelAttribute("compra") CompraDTO compraDTO,
                                       @RequestParam("planoPagamento") PlanoPagamento planoPagamento,
                                       Model model) {
        TipoPagamento[] tipos = new TipoPagamento[0];
        if (planoPagamento.equals(PlanoPagamento.APRAZO)) {
            tipos = TipoPagamento.apenasAtrasado();
        } else if (planoPagamento.equals(PlanoPagamento.AVISTA)) {
            compraDTO.setTotalParcelas(1);
            tipos = TipoPagamento.apenasAvista();
        }
        model.addAttribute("tipos_pagamento", tipos);
        model.addAttribute("compra", compraDTO);
        return "fragments/compraFrags/compraReplaces :: tiposPagamentos";
    }

    @GetMapping("/cadastro/valor-total")
    public String getValorTotal(@ModelAttribute("compra") CompraDTO compraDTO, Model model) {
        model.addAttribute("compra", compraDTO);
        return "fragments/compraFrags/compraReplaces :: valorTotalContent";
    }

    @PostMapping("/cadastro/gerar-parcelas")
    public String gerarPagamentos(@ModelAttribute("compra") CompraDTO compraDTO, Model model) {
        if (compraDTO.getTotalParcelas() < 1 || compraDTO.getTotalParcelas() > 12 || compraDTO.getCalculaValorTotalItens().compareTo(BigDecimal.ZERO) <= 0) {
            return "fragments/compraFrags/compraReplaces :: listaParcelas";
        }
        List<String> parcelas = new ArrayList<>();
        for (int i = 1; i <= compraDTO.getTotalParcelas(); i++) {
            parcelas.add(i + "x de " + JavaUtils.formatMonetaryString(compraDTO.getCalculaValorTotalItens().divide(BigDecimal.valueOf(compraDTO.getTotalParcelas()), RoundingMode.HALF_UP)));
        }
        model.addAttribute("parcelas", parcelas);
        return "fragments/compraFrags/compraReplaces :: listaParcelas";
    }

}
