package com.produto.oficina.controller;

import com.produto.oficina.model.PlanoDeContas;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import com.produto.oficina.service.PlanoDeContasService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plano-contas")
@SessionAttributes("plano")
public class PlanoDeContasController {

    private final PlanoDeContasService planoDeContasService;

    public PlanoDeContasController(PlanoDeContasService planoDeContasService) {
        this.planoDeContasService = planoDeContasService;
    }

    @ModelAttribute("plano")
    public PlanoDeContas planoDeContas() {
        return new PlanoDeContas();
    }

    @GetMapping
    public String planoList(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String filtro) {

        // Define a ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "codigo"));

        // ALTERAÇÃO PRINCIPAL AQUI:
        // Chame o novo método do service, passando o filtro e a paginação.
        Page<PlanoDeContas> planoPage = planoDeContasService.findPaginated(filtro, pageable);

        model.addAttribute("planoContasPage", planoPage);
        model.addAttribute("currentPage", page);

        // Opcional, mas bom para manter o valor do filtro no campo de busca após a submissão
        // O seu HTML já faz isso com th:value="${param.filtro}", então está ok.
        // model.addAttribute("filtro", filtro);

        return "planoContas/listPlanoContas";
    }

    @GetMapping({"/novo", "/editar/{id}"})
    public String exibirFormulario(@PathVariable(name = "id", required = false) Long id,
                                   Model model) {
        PlanoDeContas plano = (id != null) ?
                planoDeContasService.findById(id).orElse(new PlanoDeContas()) : new PlanoDeContas();

        model.addAttribute("plano", plano);
        model.addAttribute("contasPaiDisponiveis", planoDeContasService.buscarContasPaiDisponiveis());
        model.addAttribute("natureza", NaturezaContaPlanoContas.values());
        model.addAttribute("tipoPlano", TipoContaPlanoContas.values());

        return "planoContas/formPlanoContas";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("plano") PlanoDeContas plano,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("contasPaiDisponiveis", planoDeContasService.buscarContasPaiDisponiveis());
            model.addAttribute("natureza", NaturezaContaPlanoContas.values());
            model.addAttribute("tipoPlano", TipoContaPlanoContas.values());
            return "planoContas/formPlanoContas";
        }

        try {
            planoDeContasService.salvar(plano);
            redirectAttributes.addFlashAttribute("conta_salva_mensagem", "Conta salva com sucesso!");
            return "redirect:/plano-contas";

        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("contasPaiDisponiveis", planoDeContasService.buscarContasPaiDisponiveis());
            model.addAttribute("natureza", NaturezaContaPlanoContas.values());
            model.addAttribute("tipoPlano", TipoContaPlanoContas.values());
            return "planoContas/formPlanoContas";
        }
    }
}
