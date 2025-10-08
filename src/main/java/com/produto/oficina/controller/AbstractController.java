package com.produto.oficina.controller;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public abstract class AbstractController {

    protected ResponseEntity<Void> htmxRedirect(final String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("HX-Redirect", url);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    protected ResponseEntity<byte[]> gerarPdfResponseComConexao(
            String reportFileName,
            Map<String, Object> parameters,
            Connection connection,
            String outputFileName) throws JRException, IOException {

        InputStream reportStream = new ClassPathResource("templates/reports/" + reportFileName).getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", outputFileName);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    public String formatList(List<String> lista) {
        StringBuilder cond = new StringBuilder();
        for (String valor : lista) {
            cond.append("'").append(valor.toUpperCase()).append("'").append(",");
        }
        cond.deleteCharAt(cond.length() - 1);
        return cond.toString();
    }
}
