package com.produto.oficina.controller;

import com.produto.oficina.dto.reports.DRECompletoDTO;
import com.produto.oficina.model.enums.ProdutoTipo;
import com.produto.oficina.model.enums.StatusCompra;
import com.produto.oficina.model.enums.StatusOS;
import com.produto.oficina.service.PessoaService;
import com.produto.oficina.service.ProdutoService;
import com.produto.oficina.service.RelatorioService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController extends AbstractController {

    private final RelatorioService relatorioService;
    private final DataSource dataSource;
    private final PessoaService pessoaService;
    private final ProdutoService produtoService;

    public RelatorioController(RelatorioService relatorioService, DataSource dataSource, PessoaService pessoaService, ProdutoService produtoService) {
        this.relatorioService = relatorioService;
        this.dataSource = dataSource;
        this.pessoaService = pessoaService;
        this.produtoService = produtoService;
    }

    @GetMapping("/dre")
    public String dre(Model model) {
        return "relatorios/dre";
    }

    @GetMapping("/fdc")
    public String fdc(Model model) {
        return "relatorios/fdc";
    }

    @GetMapping("/estoque")
    public String estoque(Model model) {
        model.addAttribute("tipoProdutos", ProdutoTipo.values());
        return "relatorios/estoque";
    }

    @GetMapping("/osRel")
    public String ordemServico(Model model) {
        model.addAttribute("clientes", pessoaService.buscaClientes());
        model.addAttribute("statusOS", StatusOS.values());
        return "relatorios/ordemServico";
    }

    @GetMapping("/pesRel")
    public String pessoa(Model model) {
        return "relatorios/pessoa";
    }

    @GetMapping("/compraPeriodo")
    public String compraPeriodo(Model model) {
        model.addAttribute("fornecedores", pessoaService.buscaFornecedores());
        model.addAttribute("statusCompra", StatusCompra.values());
        return "relatorios/compraPeriodo";
    }

    @GetMapping("/movEstoque")
    public String movEstoque(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "relatorios/movEstoque";
    }

    @GetMapping("/faturamentoRel")
    public String faturamentoMensalAnual(Model model) {
        return "relatorios/faturamento";
    }

    @GetMapping("/contasPendentes")
    public String contasPendentes(Model model) {
        model.addAttribute("pessoas", pessoaService.buscaFornecedoresEClientes());
        return "relatorios/contasPendentes";
    }

    @GetMapping("/dre-gerar")
    public ResponseEntity<byte[]> gerarDRE(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) throws IOException, JRException {


        DRECompletoDTO dreData = relatorioService.gerarRelatorioDRE(dataInicio, dataFim);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO_RELATORIO", "Demonstrativo de Resultados");
        parametros.put("PERIODO", "De " + dataInicio.toString() + " a " + dataFim.toString());
        parametros.put("TOTAL_RECEITAS", dreData.totalReceitas());
        parametros.put("TOTAL_CUSTOS", dreData.totalCustos());
        parametros.put("TOTAL_DESPESAS", dreData.totalDespesas());
        parametros.put("LUCRO_BRUTO", dreData.lucroBruto());
        parametros.put("LUCRO_LIQUIDO", dreData.lucroLiquido());
        parametros.put("VALOR_COM_IMPOSTO", BigDecimal.ZERO);
        parametros.put("dataFim", dataFim);
        parametros.put("dataInicio", dataInicio);
        parametros.put("NOME_EMPRESA", "Oficina - Bortolucci");
        parametros.put("dataInicioFormat", dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("dataFimFormat", dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "dre/relatorio_DRE.jasper",
                    parametros,
                    connection,
                    "relatorio_DRE_" + dataInicio + "_a_" + dataFim + ".pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/fc-gerar")
    public ResponseEntity<byte[]> gerarFluxoCaixa(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) throws IOException, JRException {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dataFim", dataFim);
        parametros.put("dataInicio", dataInicio);
        parametros.put("dataInicioFormat", dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("dataFimFormat", dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "fluxoCaixa/relatorio_FDC.jasper",
                    parametros,
                    connection,
                    "rel_fluxo_caixa.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/estoque-gerar")
    public ResponseEntity<byte[]> gerarEstoque(
            @RequestParam(value = "apenasAtivos", defaultValue = "false") Boolean ativo,
            @RequestParam("tipoProduto") String tipoProduto) throws IOException, JRException {

        String cond = "";

        if (ativo) {
            cond += " prod.prod_ativo = true";
        } else {
            cond += " prod.prod_ativo = false";
        }

        switch (tipoProduto) {
            case "PECA":
                cond += " and prod.prod_tipo = 'PECA'";
                break;
            case "MATERIA_PRIMA":
                cond += " and prod.prod_tipo = 'MATERIA_PRIMA'";
                break;
            default:
                break;
        }

        String condFinal = " where " + cond;

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("condicao", condFinal);

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "estoque/relatorio_estoque.jasper",
                    parametros,
                    connection,
                    "rel_estoque.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/os-gerar")
    public ResponseEntity<byte[]> gerarOrdemServico(
            @RequestParam("status") String status,
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) throws IOException, JRException {

        LocalDateTime inicioDoPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimDoPeriodo = dataFim.atTime(LocalTime.MAX);

        String cond = " ";

        switch (status) {
            case "ABERTA":
                cond += " and os.os_status = 'ABERTA'";
                break;
            case "FINALIZADA":
                cond += " and os.os_status = 'FINALIZADA'";
                break;
            case "EM_EXECUCAO":
                cond += " and os.os_status = 'EM_EXECUCAO'";
                break;
            case "CANCELADA":
                cond += " and os.os_status = 'CANCELADA'";
                break;
            default:
                break;
        }

        if (clienteId != null) {
            cond += " and os.os_cliente_id = '" + clienteId + "'";
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("condicao", cond);
        parametros.put("dataInicio", inicioDoPeriodo);
        parametros.put("dataFim", fimDoPeriodo);
        parametros.put("dataInicioFormat", dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("dataFimFormat", dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("SUBREPORT_DIR", "templates/reports/ordemServico/");

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "ordemServico/relatorio_os.jasper",
                    parametros,
                    connection,
                    "rel_os.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/pes-gerar")
    public ResponseEntity<byte[]> gerarPessoa(
            @RequestParam("tipos") List<String> tipos,
            @RequestParam(value = "apenasAtivo", defaultValue = "false") Boolean ativo) throws IOException, JRException {

        StringBuilder cond = new StringBuilder();
        if (ativo) {
            cond.append(" pes.pes_ativo = true");
        } else {
            cond.append(" pes.pes_ativo = false");
        }

        if (!tipos.isEmpty()) {
            cond.append(" and").append(" pes.pes_tipo in (").append(formatList(tipos)).append(")");
        }

        String condFinal = " where " + cond;

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("condicao", condFinal);

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "pessoa/relatorio_pessoa.jasper",
                    parametros,
                    connection,
                    "rel_pessoa.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/compra-gerar")
    public ResponseEntity<byte[]> gerarCompraPeriodo(
            @RequestParam("statusCompra") String status,
            @RequestParam(value = "fornecedorId", required = false) Long fornecedorId,
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) throws IOException, JRException {

        LocalDateTime inicioDoPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimDoPeriodo = dataFim.atTime(LocalTime.MAX);

        String cond = " ";

        switch (status) {
            case "ABERTA":
                cond += " and c.comp_status = 'ABERTA'";
                break;
            case "FINALIZADA":
                cond += " and c.comp_status = 'FINALIZADA'";
                break;
            case "CANCELADA":
                cond += " and c.comp_status = 'CANCELADA'";
                break;
            default:
                break;
        }

        if (fornecedorId != null) {
            cond += " and c.fornecedor_id = '" + fornecedorId + "'";
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("condicao", cond);
        parametros.put("dataInicio", inicioDoPeriodo);
        parametros.put("dataFim", fimDoPeriodo);
        parametros.put("dataInicioFormat", dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("dataFimFormat", dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("SUBREPORT_DIR", "templates/reports/comprasPeriodo/");

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "comprasPeriodo/relatorio_compraPeriodo.jasper",
                    parametros,
                    connection,
                    "rel_compra_por_periodo.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/estmov-gerar")
    public ResponseEntity<byte[]> gerarMovEstoque(
            @RequestParam("tipoMovimentacao") String tipoMovimentacao,
            @RequestParam(value = "produtoId", required = false) Long prodId,
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) throws IOException, JRException {

        LocalDateTime inicioDoPeriodo = dataInicio.atStartOfDay();
        LocalDateTime fimDoPeriodo = dataFim.atTime(LocalTime.MAX);
        String cond = " ";

        switch (tipoMovimentacao) {
            case "ENTRADA":
                cond += " and me.tipo = 'ABERTA'";
                break;
            case "SAIDA":
                cond += " and me.tipo = 'FINALIZADA'";
                break;
            default:
                break;
        }

        if (prodId != null) {
            cond += " and me.produto_id = '" + prodId + "'";
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("condicao", cond);
        parametros.put("dataInicio", inicioDoPeriodo);
        parametros.put("dataFim", fimDoPeriodo);
        parametros.put("dataInicioFormat", dataInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("dataFimFormat", dataFim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "movEstoque/relatorio_movEstoque.jasper",
                    parametros,
                    connection,
                    "rel_compra_por_periodo.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/fatAnual-gerar")
    public ResponseEntity<byte[]> gerarFaturamentoAnual(@RequestParam("ano") int ano) throws IOException, JRException {


        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "faturamentoMensalAnual/relatorio_fatAnual.jasper",
                    parametros,
                    connection,
                    "rel_faturamento_anual.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/fatMensal-gerar")
    public ResponseEntity<byte[]> gerarFaturamentoMensal(@RequestParam("ano") int ano,
                                                         @RequestParam("mes") int mes) throws IOException, JRException {


        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ano", ano);
        parametros.put("mes", mes);
        parametros.put("mesString", numeromMesParaString(mes));

        try (Connection connection = dataSource.getConnection()) {
            return gerarPdfResponseComConexao(
                    "faturamentoMensalAnual/relatorio_fatMensal.jasper",
                    parametros,
                    connection,
                    "rel_faturamento_mensal.pdf"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/contasPendentes-gerar")
    public ResponseEntity<byte[]> gerarContasPendentes(@RequestParam("tipoConta") String tipoConta,
                                                       @RequestParam(required = false) LocalDate dataInicio,
                                                       @RequestParam(required = false) LocalDate dataFim,
                                                       @RequestParam(required = false) Long pessoaId,
                                                       @RequestParam(required = false) List<String> status) throws IOException, JRException {

        String condCp = " where cp.conta_pagar_status in (" + formatList(status) + ")";
        String condCr = " where cr.conta_receber_status in (" + formatList(status) + ")";

        if (dataInicio != null && dataFim != null) {
            condCp += " and cp.conta_pagar_data_vencimento between '" + dataInicio + "' and '" + dataFim + "' ";
            condCr += " and cr.conta_receber_data_vencimento between '" + dataInicio + "' and '" + dataFim + "' ";
        }

        if (pessoaId != null) {
            condCp += " and cp.fornecedor_id = '" + pessoaId + "'";
            condCr += " and cr.cliente_id = '" + pessoaId + "'";
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("dataInicio", dataInicio);
        parametros.put("dataFim", dataFim);
        parametros.put("pessoaId", pessoaId);
        parametros.put("condCr", condCr);
        parametros.put("condCp", condCp);

        if (tipoConta.equals("RECEBER")) {
            try (Connection connection = dataSource.getConnection()) {
                return gerarPdfResponseComConexao(
                        "contasPendentes/relatorio_contasReceber.jasper",
                        parametros,
                        connection,
                        "rel_contas_receber_pendentes.pdf"
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (tipoConta.equals("PAGAR")) {
            try (Connection connection = dataSource.getConnection()) {
                return gerarPdfResponseComConexao(
                        "contasPendentes/relatorio_contasPagar.jasper",
                        parametros,
                        connection,
                        "rel_contas_pagar_pendentes.pdf"
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (Connection connection = dataSource.getConnection()) {
                return gerarPdfResponseComConexao(
                        "contasPendentes/relatorio_contasAmbas.jasper",
                        parametros,
                        connection,
                        "rel_contas_pendentes.pdf"
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
