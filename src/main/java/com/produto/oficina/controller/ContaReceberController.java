package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.ContaPagar;
import com.produto.oficina.model.ContaReceber;
import com.produto.oficina.model.PlanoDeContas;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.ContaReceberService;
import com.produto.oficina.service.PessoaService;
import com.produto.oficina.service.PlanoDeContasService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contas-receber")
public class ContaReceberController {

    private final ContaReceberService contaReceberService;
    private final CaixaService caixaService;
    private final PlanoDeContasService planoDeContasService;
    private final PessoaService pessoaService;

    public ContaReceberController(ContaReceberService contaReceberService, CaixaService caixaService, PlanoDeContasService planoDeContasService, PessoaService pessoaService) {
        this.contaReceberService = contaReceberService;
        this.caixaService = caixaService;
        this.planoDeContasService = planoDeContasService;
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public String contaReceberList(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) {
        Page<ContaReceber> crPage = contaReceberService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("contaReceberPage", crPage);
        model.addAttribute("currentPage", page);
        return "contaReceber/contaReceberList";
    }

    @GetMapping("/cadastro")
    public String formContaAvulsa(Model model) {
        model.addAttribute("contaReceber", new ContaReceber());
        model.addAttribute("contasDeReceita", planoDeContasService.buscarContasPorNatureza(NaturezaContaPlanoContas.RECEITA));
        model.addAttribute("clientes", pessoaService.buscaClientes());
        return "contaReceber/formContaRecAvulsa";
    }


    @GetMapping("/visualizar/{id}")
    public String visualizarContaReceber(@PathVariable Long id, Model model) {
        model.addAttribute("contaReceber", contaReceberService.findCrById(id));
        return "contaReceber/contaReceberVisu";
    }

    @GetMapping("/registrar-recebimento/{id}")
    public String registrarRecebimento(@PathVariable Long id,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        ContaReceber contaReceber = contaReceberService.findCrById(id);
        if (contaReceberService.verificaContaReceberLegivel(contaReceber)) {
            redirectAttributes.addFlashAttribute("mostrarModalPagamento", true);
            return "redirect:/contas-receber";
        }
        model.addAttribute("tiposPagamento", TipoPagamento.apenasAprovado());
        model.addAttribute("caixaAberto", caixaService.buscaCaixaAtualAberto());
        model.addAttribute("contaReceber", contaReceber);
        return "contaReceber/contaReceberForm";
    }

    @PostMapping("/confirmar-recebimento")
    public String confirmarRecebimento(@ModelAttribute ContaReceber contaReceber,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        contaReceberService.processarRecebimentoContasReceber(contaReceber);
        redirectAttributes.addFlashAttribute("mensagem", "Recebimento da Conta ID " + contaReceber.getId() +
                " no valor de " + JavaUtils.formatMonetaryString(contaReceber.getValorRecebido()) + " registrado com sucesso!");
        redirectAttributes.addFlashAttribute("conta_recebida_mensagem", true);
        return "redirect:/contas-receber";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarContaReceber(@PathVariable Long id, Model model) {
        model.addAttribute("contaReceber", contaReceberService.findCrById(id));
        return "contaReceber/formCancelamento";
    }

    @PostMapping("/cancelar-confirmado/{id}")
    public String cancelarConfirmadoContaReceber(@PathVariable Long id,
                                                 @RequestParam("acaoFinanceira") String acaoFinanceira) {
        contaReceberService.cancelarContaReceberAvulsa(id, acaoFinanceira);
        return "redirect:/contas-receber";
    }

    @PostMapping("/salvar-avulsa")
    public String salvarContaReceberAvulsa(@ModelAttribute("contaReceber") ContaReceber contaReceber,
                                           RedirectAttributes redirectAttributes) {
        contaReceberService.criarContaReceberAvulsa(contaReceber);
        redirectAttributes.addFlashAttribute("mensagem", "Conta Receber criada com sucesso!");
        redirectAttributes.addFlashAttribute("conta_recebida_mensagem", true);
        return "redirect:/contas-receber";
    }
}
