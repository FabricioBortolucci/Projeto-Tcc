package com.produto.oficina.controller;

import ch.qos.logback.core.model.Model;
import com.produto.oficina.dto.reports.DRECompletoDTO;
import com.produto.oficina.service.RelatorioService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController extends AbstractController {

    private final RelatorioService relatorioService;
    private final DataSource dataSource;

    public RelatorioController(RelatorioService relatorioService, DataSource dataSource) {
        this.relatorioService = relatorioService;
        this.dataSource = dataSource;
    }

    @GetMapping("/dre")
    public String dre(Model model) {
        return "relatorios/dre";
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
}
