package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.ContaPagar;
import com.produto.oficina.model.ContaReceber;
import com.produto.oficina.model.enums.PlanoPagamento;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.ContaReceberService;
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

    public ContaReceberController(ContaReceberService contaReceberService, CaixaService caixaService) {
        this.contaReceberService = contaReceberService;
        this.caixaService = caixaService;
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

    @PostMapping("/cancelar/{id}")
    public String modalPagamento(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        contaReceberService.cancelarContaReceber(id);
        ContaReceber cr = contaReceberService.findCrById(id);
        redirectAttributes.addFlashAttribute("conta_recebida_mensagem", true);
        if (cr.getStatus().equals(StatusConta.PENDENTE)) {
            if (cr.getOrdemServico().getPlanoPagamento().equals(PlanoPagamento.AVISTA)) {
                redirectAttributes.addFlashAttribute("mensagem", "Estorno do recebimento da parcela [" + id + "] realizado com sucesso." +
                        " Um total de " + JavaUtils.formatMonetaryString(cr.getValor()) + " foi retirado do caixa." +
                        " A parcela agora consta como 'Pendente'.");
            } else {
                redirectAttributes.addFlashAttribute("mensagem", "Estorno do recebimento da parcela [" + id + "] realizado com sucesso." +
                        " Um total de " + JavaUtils.formatMonetaryString(cr.getValor()) + " foi adicionado como crédito com o fornecedor." +
                        " A parcela agora consta como 'Pendente'.");
            }
        } else if (cr.getStatus().equals(StatusConta.CANCELADO)) {
            redirectAttributes.addFlashAttribute("mensagem", "Parcela [" + id + "] cancelada com sucesso." +
                    " A parcela agora consta como 'Cancelado'.");
        }
        return "redirect:/contas-receber";
    }
}
