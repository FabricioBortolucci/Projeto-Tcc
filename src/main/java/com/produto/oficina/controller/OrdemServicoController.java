package com.produto.oficina.controller;

import com.produto.oficina.dto.CompraDTO;
import com.produto.oficina.model.OrdemServico;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.Servico;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.OrdemServicoService;
import com.produto.oficina.service.PessoaService;
import com.produto.oficina.service.ProdutoService;
import com.produto.oficina.service.ServicoService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ordem-servico")
@SessionAttributes("os")
public class OrdemServicoController extends AbstractController {

    private final OrdemServicoService osService;
    private final PessoaService pessoaService;
    private final ServicoService servicoService;
    private final ProdutoService produtoService;

    public OrdemServicoController(OrdemServicoService osService, PessoaService pessoaService, ServicoService servicoService, ProdutoService produtoService) {
        this.osService = osService;
        this.pessoaService = pessoaService;
        this.servicoService = servicoService;
        this.produtoService = produtoService;
    }

    @ModelAttribute("os")
    public OrdemServico ordemServico() {
        return new OrdemServico();
    }

    @GetMapping
    public String osList(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        Page<OrdemServico> osPage = osService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("ordemServicoPage", osPage);
        model.addAttribute("currentPage", page);
        return "ordemServico/osList";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("os", new OrdemServico());
        model.addAttribute("planos_pagamento", PlanoPagamento.values());
        model.addAttribute("clientes", pessoaService.buscaClientes());
        model.addAttribute("funcionarios", pessoaService.buscaFuncionarios());
        model.addAttribute("servicos", servicoService.buscaServicos());
        model.addAttribute("produtos", produtoService.buscaProdutosAtivos());
        return "ordemServico/osForm";
    }

    @GetMapping("/cadastro/buscar-planoPag")
    public String buscarPlanoPagamento(@ModelAttribute("os") OrdemServico ordemServico,
                                       @RequestParam("planoPagamento") PlanoPagamento planoPagamento,
                                       Model model) {
        TipoPagamento[] tipos = new TipoPagamento[0];
        if (planoPagamento.equals(PlanoPagamento.APRAZO)) {
            tipos = TipoPagamento.apenasAtrasado();
        } else if (planoPagamento.equals(PlanoPagamento.AVISTA)) {
            tipos = TipoPagamento.apenasAvista();
        }
        model.addAttribute("tipos_pagamento", tipos);
        model.addAttribute("os", ordemServico);
        return "fragments/compraFrags/compraReplaces :: tiposPagamentos";
    }

    @GetMapping("/cadastro/busca-preco-servico")
    public String buscaPrecoServico(Model model,
                                    @RequestParam("idServico") Long idServico) {
        Servico serv = servicoService.buscaServico(idServico);
        model.addAttribute("servicoSelected", serv);
        return "ordemServico/osForm :: #valorUnitarioServico";
    }

    @GetMapping("/cadastro/busca-preco-prods")
    public String buscaPrecoProds(Model model,
                                  @RequestParam("idProds") Long idProd) {
        Produto prod = produtoService.findById(idProd);
        model.addAttribute("prodSelected", prod);
        return "ordemServico/osForm :: #valorUnitarioProd";
    }

    @PostMapping("/cadastro/adicionar-servico-lista")
    public String adicionaServicoLista(@ModelAttribute("os") OrdemServico ordemServico,
                                       @RequestParam("valorUnitarioServico") BigDecimal valorServico,
                                       @RequestParam("quantidadeServicoName") Integer qtdServico,
                                       @RequestParam("idServico") Long idServico,
                                       Model model) {
        osService.adicionaServicoLista(valorServico, qtdServico, ordemServico, idServico);
        model.addAttribute("os", ordemServico);
        return "ordemServico/osForm :: tabelaServicos";
    }

    @PostMapping("/cadastro/adicionar-produto-lista")
    public String adicionaProdLista(@ModelAttribute("os") OrdemServico ordemServico,
                                    @RequestParam("valorUnitarioProd") BigDecimal valorProduto,
                                    @RequestParam("quantidadeProdName") Integer qtdProduto,
                                    @RequestParam("idProds") Long idProd,
                                    Model model) {
        osService.adicionaProdutoLista(valorProduto, qtdProduto, ordemServico, idProd);
        model.addAttribute("os", ordemServico);
        return "ordemServico/osForm :: tabelaProdutos";
    }

    @DeleteMapping("/cadastro/remover-servico-lista/{index}")
    public String removerServicoLista(@ModelAttribute("os") OrdemServico ordemServico,
                                      @PathVariable int index,
                                      Model model) {
        ordemServico.getItensServico().remove(index);
        model.addAttribute("os", ordemServico);
        return "ordemServico/osForm :: tabelaServicos";
    }

    @DeleteMapping("/cadastro/remover-produto-lista/{index}")
    public String removerProdutoLista(@ModelAttribute("os") OrdemServico ordemServico,
                                      @PathVariable int index,
                                      Model model) {
        ordemServico.getPecasUsadas().remove(index);
        model.addAttribute("os", ordemServico);
        return "ordemServico/osForm :: tabelaProdutos";
    }

    @PostMapping("/cadastro/salvar-rascunho")
    public Object salvarOsRascunho(@ModelAttribute("os") OrdemServico ordemServico) {
        osService.salvarRascunho(ordemServico);
        return htmxRedirect("/ordem-servico");
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarOs(@PathVariable Long id, Model model) {
        osService.cancelarOS(id);
        return "redirect:/ordem-servico";
    }

    @PostMapping("/cadastro/gerar-parcelas")
    public String gerarPagamentos(@ModelAttribute("os") OrdemServico ordemServico, Model model) {
        if (ordemServico.getQuantParcelas() < 1 || ordemServico.getQuantParcelas() > 12 || ordemServico.getCalculaTotalProdsItens().add(ordemServico.getCalculaTotalServicoItens()).compareTo(BigDecimal.ZERO) <= 0) {
            return "fragments/ordemServicoFragments/osReplace :: listaParcelas";
        }
        ordemServico.setParcelas(new ArrayList<>());
        for (int i = 1; i <= ordemServico.getQuantParcelas(); i++) {
            ordemServico.getParcelas().add((ordemServico.getCalculaTotalProdsItens().add(ordemServico.getCalculaTotalServicoItens())).divide(BigDecimal.valueOf(ordemServico.getQuantParcelas()), RoundingMode.HALF_UP));
        }
        return "fragments/ordemServicoFragments/osReplace :: listaParcelas";
    }

}
