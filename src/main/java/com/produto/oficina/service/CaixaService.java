package com.produto.oficina.service;

import com.produto.oficina.model.Caixa;
import com.produto.oficina.model.Pessoa;
import com.produto.oficina.model.enums.StatusCaixa;
import com.produto.oficina.repository.CaixaRepository;
import com.produto.oficina.repository.MovimentacaoCaixaRepository;
import com.produto.oficina.repository.PessoaRepository;
import com.produto.oficina.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CaixaService {


    private final CaixaRepository caixaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;

    public CaixaService(CaixaRepository caixaRepository, UsuarioRepository usuarioRepository, PessoaRepository pessoaRepository, MovimentacaoCaixaRepository movimentacaoCaixaRepository) {
        this.caixaRepository = caixaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pessoaRepository = pessoaRepository;
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
    }

    public Page<Caixa> findAll(Pageable pageable) {
        return caixaRepository.findAll(pageable);
    }

    public Caixa findById(Long index) {
        return caixaRepository.findById(index).get();
    }

    @Transactional
    public Caixa novoCaixa(Pessoa usuarioAbertura) {
        BigDecimal valorAberturaNovoCaixa = BigDecimal.ZERO;
        String observacaoHistorico = "";

        Optional<Caixa> ultimoCaixa = caixaRepository.findTopByStatusOrderByDataFechamentoDesc(StatusCaixa.FECHADO);
        if (ultimoCaixa.isPresent()) {
            Caixa ultimoCaixaFechado = ultimoCaixa.get();
            if (ultimoCaixaFechado.getValorFechamentoContado() != null) {
                valorAberturaNovoCaixa = ultimoCaixaFechado.getValorFechamentoContado();
                observacaoHistorico = " Aberto com saldo do caixa anterior (ID: " + ultimoCaixaFechado.getId() +
                        ", Fechado em: " + ultimoCaixaFechado.getDataFechamento().toLocalDate() + ").";
            } else {
                observacaoHistorico = " Último caixa (ID: " + ultimoCaixaFechado.getId() +
                        ") não possui valor de fechamento contado registrado. Iniciando com R$0,00.";
            }
        } else {
            observacaoHistorico = " Nenhum caixa anterior encontrado. Iniciando com R$0,00.";
        }

        return new Caixa(
                usuarioAbertura,
                valorAberturaNovoCaixa,
                observacaoHistorico.trim()
        );
    }

    public boolean verificaCaixaAberto() {
        return caixaRepository.existsByStatus(StatusCaixa.ABERTO);
    }


}
