package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.MovimentacaoCaixa;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.CompraService;
import com.produto.oficina.service.PessoaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/caixa")
public class CaixaController {

    private final CaixaService caixaService;
    private final PessoaService pessoaService;

    public CaixaController(CaixaService caixaService, PessoaService pessoaService) {
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public String caixaList(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size) {
        Page<Caixa> caixaPage = caixaService.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("caixaPage", caixaPage);
        model.addAttribute("currentPage", page);
        return "caixa/caixaList";
    }

    @GetMapping("/cadastro")
    public String caixaForm(RedirectAttributes redirectAttributes, Model model) {
        if (caixaService.verificaCaixaAberto()) {
            redirectAttributes.addFlashAttribute("mostrarModal", true);
            return "redirect:/caixa";
        }
        model.addAttribute("caixa", caixaService.novoCaixa(pessoaService.buscaPessoaLogada()));
        return "caixa/caixaForm";
    }

    @PostMapping("/cadastrar")
    public String caixaSave(@ModelAttribute Caixa caixa, RedirectAttributes redirectAttributes, Model model) {
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("mensagem", "Salvo com succeso!");
        return "redirect:/caixa";
    }

    @PostMapping("/fechar/{index}")
    public String fecharCaixa(@PathVariable Long index, RedirectAttributes redirectAttributes, Model model) {
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("mensagem", "Fechado com succeso!");
        return "redirect:/caixa";
    }

    @GetMapping("/editar/{index}")
    public String caixaEdit(@PathVariable Long index, Model model) {
        model.addAttribute("caixa", caixaService.findById(index));
        return "caixa/caixaForm";
    }

    @GetMapping("/visualizar/{index}")
    public String visualizarCaixa(@PathVariable Long index,
                                  Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        return "caixa/caixaVisu";
    }

    // Movimentação de Fundos no caixa

    @GetMapping("/cadastro/mostrarModalFundos")
    public String showModalFundos(Model model) {
        model.addAttribute("movimento", new MovimentacaoCaixa());
        model.addAttribute("tiposMovimento", TipoMovimentacao.values());
        model.addAttribute("mostrarModal", true);
        return "fragments/caixaFragments/modalCaixa";
    }

    @PostMapping("/cadastro/adicionarFundos")
    public String addFundos(@ModelAttribute MovimentacaoCaixa movimento,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        return null;
    }


}
