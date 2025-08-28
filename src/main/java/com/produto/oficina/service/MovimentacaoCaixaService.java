package com.produto.oficina.service;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.LancamentoFinanceiro;
import com.produto.oficina.model.MovimentacaoCaixa;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.repository.LancamentoFinanceiroRepository;
import com.produto.oficina.repository.MovimentacaoCaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoCaixaService {


    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public MovimentacaoCaixaService(MovimentacaoCaixaRepository movimentacaoCaixaRepository, CaixaService caixaService, PessoaService pessoaService, LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
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
            if (movimento.getTipo().equals(TipoMovimentacao.SAIDA) &&
                    (movimento.getPlanoDeContas().getNaturezaConta() == NaturezaContaPlanoContas.DESPESA ||
                            movimento.getPlanoDeContas().getNaturezaConta() == NaturezaContaPlanoContas.CUSTO)) {

                LancamentoFinanceiro lancamentoDespesa = new LancamentoFinanceiro(
                        "Despesa avulsa: " + (movimento.getDescricao() != null ? movimento.getDescricao() : movimento.getPlanoDeContas().getDescricao()),
                        movimento.getValor(),
                        LocalDate.now(),
                        movimento.getPlanoDeContas(),
                        null
                );
                lancamentoFinanceiroRepository.save(lancamentoDespesa);

            } else if (movimento.getTipo().equals(TipoMovimentacao.ENTRADA) &&
                    movimento.getPlanoDeContas().getNaturezaConta() == NaturezaContaPlanoContas.RECEITA) {

                LancamentoFinanceiro lancamentoReceita = new LancamentoFinanceiro(
                        "Receita avulsa: " + (movimento.getDescricao() != null ? movimento.getDescricao() : movimento.getPlanoDeContas().getDescricao()),
                        movimento.getValor(),
                        LocalDate.now(),
                        movimento.getPlanoDeContas(),
                        null
                );
                lancamentoFinanceiroRepository.save(lancamentoReceita);
            }

            if (caixaAtual != null) {
                movimento.setCaixa(caixaAtual);
                movimento.setDataMovimentacao(LocalDateTime.now());
                caixaAtual.getMovimentacoes().add(movimento);
                movimentacaoCaixaRepository.saveAndFlush(movimento);
                caixaService.salvarCaixaAposMovimento(caixaAtual);
            }
        }
    }

}
