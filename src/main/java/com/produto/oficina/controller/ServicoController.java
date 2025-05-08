package com.produto.oficina.controller;

import com.produto.oficina.model.Servico;
import com.produto.oficina.service.ServicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servico")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public String servicoList(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {
        Page<Servico> servicoPage = servicoService.buscaTodosPaginado(PageRequest.of(page, size));
        model.addAttribute("servicoPage", servicoPage);
        model.addAttribute("currentPage", page);
        return "servico/servList";
    }

    @GetMapping("/cadastro")
    public String servicoForm(Model model) {
        model.addAttribute("servico", new Servico());
        return "servico/servForm";
    }

    @PostMapping("/cadastrar")
    public String servicoSave(@ModelAttribute Servico servico, RedirectAttributes redirectAttributes) {
        servicoService.salvar(servico);
        redirectAttributes.addFlashAttribute("servico_cadastrado", true);
        return "redirect:/servico";
    }

    @GetMapping("/editar/{index}")
    public String servicoEditar(@PathVariable Long index, Model model) {
        model.addAttribute("servico", servicoService.buscaServico(index));
        return "servico/servForm";
    }

    @GetMapping("/visualizar/{index}")
    public String servicoVisualizar(@PathVariable Long index, Model model) {
        model.addAttribute("servico", servicoService.buscaServico(index));
        return "servico/servVisu";
    }

    @PostMapping("/desativar/{index}")
    public String servicoDesativar(@PathVariable Long index) {
        servicoService.desativarServico(index);
        return "redirect:/servico";
    }

}
