package com.produto.oficina.service;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.MovimentacaoCaixa;
import com.produto.oficina.repository.MovimentacaoCaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoCaixaService {


    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final CaixaService caixaService;

    public MovimentacaoCaixaService(MovimentacaoCaixaRepository movimentacaoCaixaRepository, CaixaService caixaService) {
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.caixaService = caixaService;
    }

    public List<MovimentacaoCaixa> buscarTodasMovimentacoesPorCaixa(Long idCaixa) {
        return movimentacaoCaixaRepository.findAllByCaixa_Id(idCaixa);
    }


    @Transactional
    public void salvarMovimentacao(MovimentacaoCaixa movimento, Long caixaId) {
        if (movimento != null) {
            Caixa caixaAtual = caixaService.findById(caixaId);
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
