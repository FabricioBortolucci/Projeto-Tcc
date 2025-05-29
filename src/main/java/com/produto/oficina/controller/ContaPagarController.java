package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.ContaPagar;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contas-pagar")
public class ContaPagarController {

    private final ContaPagarService contaPagarService;
    private final CaixaService caixaService;

    public ContaPagarController(ContaPagarService contaPagarService, CaixaService caixaService) {
        this.contaPagarService = contaPagarService;
        this.caixaService = caixaService;
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
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        contaPagarService.processarPagamentoContasPagar(contaPagar);
        redirectAttributes.addFlashAttribute("mensagem", "Pagamento da Conta ID " + contaPagar.getId() +
                " no valor de " + JavaUtils.formatMonetaryString(contaPagar.getValorPago()) + " registrado com sucesso!");
        redirectAttributes.addFlashAttribute("conta_paga_mensagem", true);
        return "redirect:/contas-pagar";
    }

    @PostMapping("/cancelar/{id}")
    public String modalPagamento(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        contaPagarService.cancelarContaPagar(id);
        ContaPagar cp = contaPagarService.findCpById(id);
        redirectAttributes.addFlashAttribute("conta_paga_mensagem", true);
        if (cp.getStatus().equals(StatusConta.PENDENTE)) {
            redirectAttributes.addFlashAttribute("mensagem", "Estorno do pagamento da parcela [" + id + "] realizado com sucesso." +
                    " Um total de " + JavaUtils.formatMonetaryString(cp.getValor()) + " foi adicionado como crédito com o fornecedor." +
                    " A parcela agora consta como 'Pendente'.");
        } else if (cp.getStatus().equals(StatusConta.CANCELADO)) {
            redirectAttributes.addFlashAttribute("mensagem", "Parcela [" + id + "] cancelada com sucesso." +
                    " A parcela agora consta como 'Cancelado'.");
        }
        return "redirect:/contas-pagar";
    }

}
