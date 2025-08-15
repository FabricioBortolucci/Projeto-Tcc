package com.produto.oficina.service;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.MovimentacaoCaixa;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.repository.MovimentacaoCaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoCaixaService {


    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;

    public MovimentacaoCaixaService(MovimentacaoCaixaRepository movimentacaoCaixaRepository, CaixaService caixaService, PessoaService pessoaService) {
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
    }

    public List<MovimentacaoCaixa> buscarTodasMovimentacoesPorCaixa(Long idCaixa) {
        return movimentacaoCaixaRepository.findAllByCaixa_IdOrderByIdDesc(idCaixa);
    }


    @Transactional
    public void salvarMovimentacao(MovimentacaoCaixa movimento, Long caixaId, Long idCliente) {
        if (movimento != null) {
            Caixa caixaAtual = caixaService.findById(caixaId);
            if (idCliente != null) {
                Pessoa cliente = pessoaService.buscaClientePorId(idCliente);
                if (cliente.getPesCredito().compareTo(BigDecimal.ZERO) > 0) {
                    cliente.setPesCredito(cliente.getPesCredito().subtract(movimento.getValor()));
                }
                pessoaService.salvarEdit(cliente);
            }
            if (caixaAtual != null) {
                caixaAtual.getMovimentacoes().add(movimento);
                movimento.setCaixa(caixaAtual);
                movimento.setDataMovimentacao(LocalDateTime.now());
                movimentacaoCaixaRepository.saveAndFlush(movimento);
                caixaService.salvarCaixaAposMovimento(caixaAtual);
            }
        }
    }

}
