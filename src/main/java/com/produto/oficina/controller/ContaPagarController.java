package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.ContaPagar;
import com.produto.oficina.model.ContaReceber;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/contas-pagar")
public class ContaPagarController {

    private final ContaPagarService contaPagarService;
    private final CaixaService caixaService;
    private final PlanoDeContasService planoDeContasService;
    private final PessoaService pessoaService;
    private final ContaReceberService contaReceberService;

    public ContaPagarController(ContaPagarService contaPagarService, CaixaService caixaService, PlanoDeContasService planoDeContasService, PessoaService pessoaService, ContaReceberService contaReceberService) {
        this.contaPagarService = contaPagarService;
        this.caixaService = caixaService;
        this.planoDeContasService = planoDeContasService;
        this.pessoaService = pessoaService;
        this.contaReceberService = contaReceberService;
    }


    @GetMapping
    public String contaPagarList(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size) {
        Page<ContaPagar> cpPage = contaPagarService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("contaPagarPage", cpPage);
        model.addAttribute("currentPage", page);
        return "contaPagar/contaPagarList";
    }

    @GetMapping("/visualizar/{id}")
    public String visualizarContaPagar(@PathVariable Long id, Model model) {
        model.addAttribute("contaPagar", contaPagarService.findCpById(id));
        return "contaPagar/contaPagarVisu";
    }

    @GetMapping("/cadastro")
    public String formContaAvulsa(Model model) {
        model.addAttribute("contaPagar", new ContaPagar());
        List<NaturezaContaPlanoContas> naturezasDeSaida = Arrays.asList(
                NaturezaContaPlanoContas.DESPESA,
                NaturezaContaPlanoContas.CUSTO
        );
        model.addAttribute("planoDeContas", planoDeContasService.buscarContasIn(TipoContaPlanoContas.ANALITICA, naturezasDeSaida));
        model.addAttribute("fornecedores", pessoaService.buscaFornecedores());
        return "contaPagar/formContaPagAvulsa";
    }

    @GetMapping("/registrar-pagamento/{id}")
    public String registrarPagamento(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        ContaPagar contaPagar = contaPagarService.findCp(id);
        if (contaPagarService.verificaContaPagarLegivel(contaPagar)) {
            redirectAttributes.addFlashAttribute("mostrarModalPagamento", true);
            return "redirect:/contas-pagar";
        }
        model.addAttribute("tipoPagamento", TipoPagamento.apenasAprovado());
        model.addAttribute("caixaAberto", caixaService.buscaCaixaAtualAberto());
        model.addAttribute("contaPagar", contaPagar);
        return "contaPagar/contaPagarForm";
    }

    @PostMapping("/confirmar-pagamento")
    public String confirmarPagamento(@ModelAttribute ContaPagar contaPagar,
                                     @RequestParam("usarCredito") String usarCredito,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        contaPagarService.processarPagamentoContasPagar(contaPagar, usarCredito);
        redirectAttributes.addFlashAttribute("mensagem", "Pagamento da Conta ID " + contaPagar.getId() +
                " no valor de " + JavaUtils.formatMonetaryString(contaPagar.getValorPago()) + " registrado com sucesso!");
        redirectAttributes.addFlashAttribute("conta_paga_mensagem", true);
        return "redirect:/contas-pagar";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarContaPagar(@PathVariable Long id,
                                     Model model) {
        model.addAttribute("contaPagar", contaPagarService.findCpById(id));
        return "contaPagar/formCancelamento";
    }

    @PostMapping("/cancelar-confirmado/{id}")
    public String cancelarConfirmadoContaPagar(@PathVariable Long id,
                                               @RequestParam("acaoFinanceira") String usarCredito) {
        contaPagarService.cancelarContaPagarAvulsa(id, usarCredito);
        return "redirect:/contas-pagar";
    }

    @PostMapping("/salvar-avulsa")
    public String salvarContaPagarAvulsa(@ModelAttribute("contaPagar") ContaPagar contaPagar,
                                         RedirectAttributes redirectAttributes) {
        contaPagarService.criarContaPagarAvulsa(contaPagar);
        redirectAttributes.addFlashAttribute("mensagem", "Conta Pagar criada com sucesso!");
        redirectAttributes.addFlashAttribute("conta_paga_mensagem", true);
        return "redirect:/contas-pagar";
    }

    @GetMapping("/aplicar-credito")
    public String aplicarCreditoContaPagar(Model model,
                                           @RequestParam("usarCredito") String credito) {
        if (credito.equals("S_CR")) {
            model.addAttribute("tipoPagamento", TipoPagamento.onlyCredito());
        } else {
            model.addAttribute("tipoPagamento", TipoPagamento.apenasAprovado());
        }
        return "fragments/contaPagarFrags/contaPagarReplaces :: pagamentoCP";
    }
}
