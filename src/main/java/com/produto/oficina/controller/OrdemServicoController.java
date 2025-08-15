package com.produto.oficina.controller;

import com.produto.oficina.model.OrdemServico;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.Servico;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final CaixaService caixaService;

    public OrdemServicoController(OrdemServicoService osService, PessoaService pessoaService, ServicoService servicoService, ProdutoService produtoService, CaixaService caixaService) {
        this.osService = osService;
        this.pessoaService = pessoaService;
        this.servicoService = servicoService;
        this.produtoService = produtoService;
        this.caixaService = caixaService;
    }

    @ModelAttribute("os")
    public OrdemServico ordemServico() {
        return new OrdemServico();
    }

    @GetMapping
    public String osList(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        Page<OrdemServico> osPage = osService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,  "id")));
        model.addAttribute("ordemServicoPage", osPage);
        model.addAttribute("currentPage", page);
        return "ordemServico/osList";
    }

    @GetMapping("/cadastro")
    public String cadastro(RedirectAttributes redirectAttributes, Model model) {
        if (!caixaService.verificaCaixaAberto()) {
            redirectAttributes.addFlashAttribute("mostrarModal", true);
            return "redirect:/ordem-servico";
        }
        model.addAttribute("os", new OrdemServico());
        model.addAttribute("clientes", pessoaService.buscaClientes());
        model.addAttribute("funcionarios", pessoaService.buscaFuncionarios());
        model.addAttribute("servicos", servicoService.buscaServicos());
        model.addAttribute("produtos", produtoService.buscaProdutosAtivos());
        return "ordemServico/osForm";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model) {
        model.addAttribute("os", osService.findById(id));
        model.addAttribute("clientes", pessoaService.buscaClientes());
        model.addAttribute("funcionarios", pessoaService.buscaFuncionarios());
        model.addAttribute("servicos", servicoService.buscaServicos());
        model.addAttribute("produtos", produtoService.buscaProdutosAtivos());
        return "ordemServico/osForm";
    }

    @GetMapping("/visualizar/{id}")
    public String visualizar(Model model, @PathVariable Long id) {
        model.addAttribute("os", osService.findById(id));
        return "ordemServico/osVisu";
    }

    @PostMapping("/cadastro/validar-finalizacao")
    public Object validarParaFinalizacao(
            @ModelAttribute("os") OrdemServico ordemServico,
            Model model) {

        List<String> erros = new ArrayList<>();

        if (ordemServico.getCliente() == null || ordemServico.getCliente().getId() == null) {
            erros.add("É necessário selecionar um Cliente.");
        }
        if (ordemServico.getFuncionario() == null || ordemServico.getFuncionario().getId() == null) {
            erros.add("É necessário selecionar um Funcionário Responsável.");
        }
        if (ordemServico.getItensServico() == null || ordemServico.getItensServico().isEmpty()) {
            erros.add("A Ordem de Serviço deve conter pelo menos um serviço prestado.");
        }
        if (ordemServico.getPecasUsadas() == null || ordemServico.getPecasUsadas().isEmpty()) {
            erros.add("A Ordem de Serviço deve conter pelo menos uma peça/produto utilizado.");
        }

        if (erros.isEmpty()) {
            return htmxRedirect("/ordem-servico/finalizar-os");
        } else {
            model.addAttribute("errosValidacao", erros);
            return "fragments/modals/validacaoFinalizacaoModal :: modalContent";
        }
    }

    @GetMapping("/finalizar-os")
    public String preparafinalizarOs(@ModelAttribute("os") OrdemServico ordemServico,
                                     Model model) {
        model.addAttribute("os", osService.preparaFinalizacao(ordemServico));
        model.addAttribute("planos_pagamento", PlanoPagamento.values());
        return "ordemServico/osFinalizacao";
    }

    @GetMapping("/finalizar-view/{id}")
    public String preparafinalizarOsView(@PathVariable Long id,
                                         @ModelAttribute("os") OrdemServico ordemServico,
                                         Model model) {
        model.addAttribute("os", osService.preparaFinalizacaoView(id));
        model.addAttribute("planos_pagamento", PlanoPagamento.values());
        return "ordemServico/osFinalizacao";
    }

    @PostMapping("/finalizar-confirmado")
    public String finalizarConfirmarOs(@ModelAttribute("os") OrdemServico ordemServico) {
        osService.finalizarOs(ordemServico);
        return "redirect:/ordem-servico";
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

    @GetMapping("/excluir-os/{id}")
    public String excluirOs(@PathVariable Long id, Model model) {
        osService.cancelarOS(id);
        return "redirect:/ordem-servico";
    }

    @GetMapping("/gerar-parcelas")
    public String gerarPagamentos(@ModelAttribute("os") OrdemServico ordemServico, Model model) {
        if (ordemServico.getQuantParcelas() < 1 || ordemServico.getQuantParcelas() > 12 || ordemServico.getCalculaTotalProdsItens().add(ordemServico.getCalculaTotalServicoItens()).compareTo(BigDecimal.ZERO) <= 0) {
            return "fragments/ordemServicoFragments/osReplace :: listaParcelas";
        }
        ordemServico.setParcelas(new ArrayList<>());
        for (int i = 1; i <= ordemServico.getQuantParcelas(); i++) {
            ordemServico.getParcelas().add((ordemServico.getCalculaTotalProdsItens().add(ordemServico.getCalculaTotalServicoItens())).divide(BigDecimal.valueOf(ordemServico.getQuantParcelas()), RoundingMode.HALF_UP));
        }
        model.addAttribute("os", ordemServico);
        return "fragments/ordemServicoFragments/osReplace :: listaParcelas";
    }

    @GetMapping("/executar-os/{id}")
    public String executarOs(@PathVariable Long id) {
        osService.executarOs(id);
        return "redirect:/ordem-servico";
    }

    @GetMapping("/cancelar-os/{id}")
    public String cancelarOs(@PathVariable Long id,
                             Model model) {
        model.addAttribute("os", osService.findById(id));
        return "ordemServico/osCancelamento";
    }

    @PostMapping("/cancelar-confirmado")
    public String cancelarConfirmado(
            @ModelAttribute("os") OrdemServico ordemServico,
            @RequestParam("acaoFinanceira") String acaoFinanceira,
            @RequestParam(name = "pecasParaDevolver", required = false) List<Long> idsDasPecasParaDevolver,
            RedirectAttributes redirectAttributes) {
        osService.cancelarOrdemDeServico(ordemServico, idsDasPecasParaDevolver, acaoFinanceira);
        redirectAttributes.addFlashAttribute("os_mensagem_sucesso", "Ordem de Serviço #" + ordemServico.getId() + " cancelada com sucesso!");
        return "redirect:/ordem-servico";
    }

}
