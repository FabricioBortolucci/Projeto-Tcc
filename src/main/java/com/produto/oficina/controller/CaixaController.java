package com.produto.oficina.controller;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.MovimentacaoCaixa;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
@RequestMapping("/caixa")
public class CaixaController extends AbstractController {

    private final CaixaService caixaService;
    private final PessoaService pessoaService;
    private final MovimentacaoCaixaService mvService;
    private final PlanoDeContasService  planoDeContasService;

    public CaixaController(CaixaService caixaService, PessoaService pessoaService, MovimentacaoCaixaService mvService, PlanoDeContasService planoDeContasService) {
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
        this.mvService = mvService;
        this.planoDeContasService = planoDeContasService;
    }

    private Long caixaId = 0L;

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
    public String caixaForm(@RequestParam(required = false, defaultValue = "false") boolean isHome, RedirectAttributes redirectAttributes, Model model) {
        if (isHome) {
            return "redirect:/caixa";
        }
        if (caixaService.verificaCaixaAberto()) {
            redirectAttributes.addFlashAttribute("mostrarModal", true);
            return "redirect:/caixa";
        }
        model.addAttribute("caixa", caixaService.novoCaixa(pessoaService.buscaPessoaLogada()));
        model.addAttribute("caixa_cadastrado", true);
        model.addAttribute("listaMovimentacao", new ArrayList<>());
        model.addAttribute("mensagem", "Caixa cadastrado com sucesso!");
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
        model.addAttribute("listaMovimentacao", mvService.buscarTodasMovimentacoesPorCaixa(index));
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

    @GetMapping("/editar/mostrarModalFundos/{id}")
    public String showModalFundos(@PathVariable Long id,
                                  @RequestParam(required = false, defaultValue = "false") boolean remover,
                                  Model model) {
        caixaId = id;
        if (remover) {
            model.addAttribute("remover", remover);
            model.addAttribute("modal_title", "Remover fundos do caixa");
            model.addAttribute("modal_label", "Valor a ser removido: ");
            model.addAttribute("tiposMovimento", TipoMovimentacao.apenasSaidas());
            model.addAttribute("contasParaSaida", planoDeContasService.buscarContasParaSaidaDeCaixa());
        } else {
            model.addAttribute("remover", remover);
            model.addAttribute("modal_title", "Adicionar fundos no caixa");
            model.addAttribute("modal_label", "Valor a ser adicionado: ");
            model.addAttribute("tiposMovimento", TipoMovimentacao.apenasEntradas());
            model.addAttribute("contasParaEntrada", planoDeContasService.buscarContasParaEntradaDeCaixa());
        }
        model.addAttribute("movimento", new MovimentacaoCaixa());
        model.addAttribute("mostrarModal", true);
        return "fragments/caixaFragments/modalCaixa";
    }

    @GetMapping("/show-clientes")
    public String showClientes(Model model) {
        model.addAttribute("clientes", pessoaService.buscarClientesAtivosComCredito());
        return "fragments/caixaFragments/clientesCredito";
    }

    @GetMapping("/hide-clientes")
    public String hideClientes() {
        return "fragments/caixaFragments/modalCaixa :: #modalClientesCredito";
    }

    @PostMapping("/editar/movimentarFundos")
    public String addFundos(@ModelAttribute MovimentacaoCaixa movimento,
                            @RequestParam(value = "clienteSelect", required = false) Long idCliente,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        mvService.salvarMovimentacao(movimento, caixaId, idCliente);
        redirectAttributes.addFlashAttribute("mensagem", "Movimento realizado com sucesso!");
        return "redirect:/caixa/editar/" + movimento.getCaixa().getId();
    }

    @GetMapping("/editar/fechamento/{id}")
    public String fechamentoDeCaixa(@PathVariable Long id, Model model) {
        model.addAttribute("caixa", caixaService.findById(id));
        return "caixa/caixaFechamentoForm";
    }

    @PostMapping("/editar/fechar")
    public String fecharCaixa(@ModelAttribute("caixa") Caixa caixaDoFormulario,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (caixaId == null) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro: ID do Caixa não foi fornecido.");
            return "redirect:/caixa";
        }
        caixaService.fecharCaixa(caixaDoFormulario, pessoaService.buscaPessoaLogada());

        redirectAttributes.addFlashAttribute("mensagem", "Caixa ID " + caixaId + " fechado com sucesso!");
        return "redirect:/caixa";
    }


    @PostMapping("/editar/fechamento/calcular-diferenca-fragmento")
    public String calcularDiferencaFragmento(
            @RequestParam("saldoEsperadoSistema") String saldoEsperadoStr,
            @RequestParam("valorFechamentoContado") String valorFechamentoContadoStr,
            Model model) {

        // O JavaScript já deve enviar valores "limpos" (ex: "1250.75")
        // devido ao htmx:configRequest
        BigDecimal saldoEsperado = JavaUtils.parseMonetaryString(saldoEsperadoStr);
        BigDecimal valorContado = JavaUtils.parseMonetaryString(valorFechamentoContadoStr);

        BigDecimal diferenca = valorContado.subtract(saldoEsperado);

        model.addAttribute("diferencaCalculada", diferenca);
        model.addAttribute("valorPuroDiferenca", diferenca.toPlainString()); // Para o data-valor-puro

        // Retorna o caminho para o fragmento Thymeleaf
        return "fragments/caixaFragments/fragDiff :: fragmentoDiferencaCaixa";
    }


}
