package com.produto.oficina.controller;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.Compra;
import com.produto.oficina.service.CaixaService;
import com.produto.oficina.service.CompraService;
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

    public CaixaController(CaixaService caixaService) {
        this.caixaService = caixaService;
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
        if (!caixaService.verificaCaixaAberto()) {
            redirectAttributes.addFlashAttribute("mostrarModal", true);
            return "redirect:/caixa";
        }

        model.addAttribute("caixa", caixaService.novoCaixa());
        return "caixa/caixaForm";
    }

    @PostMapping("/cadastrar")
    public String caixaSave(@ModelAttribute Caixa caixa, RedirectAttributes redirectAttributes, Model model) {
        caixaService.save(caixa);
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("mensagem", "Salvo com succeso!");
        return "redirect:/caixa";
    }

    @PostMapping("/fechar/{index}")
    public String fecharCaixa(@PathVariable Long index, RedirectAttributes redirectAttributes, Model model) {
        caixaService.fecharCaixa(index);
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("mensagem", "Fechado com succeso!");

        return "redirect:/caixa";
    }

    @GetMapping("/editar/{index}")
    public String caixaEdit(@PathVariable Long index, Model model) {
        model.addAttribute("caixa", caixaService.findById(index).get());
        return "caixa/caixaForm";
    }

    @GetMapping("/visualizar/{index}")
    public String visualizarCaixa(@PathVariable Long index,
                                  Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        model.addAttribute("caixa", caixaService.buscarCaixaView(index));
        model.addAttribute("mvsCaixa", caixaService.buscaMovimentacaoCaixa(index));
        return "caixa/caixaVisu";
    }

}
