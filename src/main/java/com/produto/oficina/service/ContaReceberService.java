package com.produto.oficina.service;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.repository.ContaReceberRepository;
import com.produto.oficina.repository.LancamentoFinanceiroRepository;
import com.produto.oficina.repository.OrdemServicoRepository;
import com.produto.oficina.repository.PlanoDeContasRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;
    private final PlanoDeContasRepository planoDeContasRepository;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public ContaReceberService(ContaReceberRepository contaReceberRepository, OrdemServicoRepository ordemServicoRepository, CaixaService caixaService, PessoaService pessoaService, PlanoDeContasRepository planoDeContasRepository, LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.contaReceberRepository = contaReceberRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
        this.planoDeContasRepository = planoDeContasRepository;
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
    }

    public Page<ContaReceber> findAll(Pageable pageable) {
        return contaReceberRepository.findAll(pageable);
    }

    public List<ContaReceber> findAllByOsId(Long id) {
        return contaReceberRepository.findAllByOrdemServico_Id(id);
    }

    public ContaReceber findCrById(Long id) {
        return contaReceberRepository.findById(id).get();
    }

    public boolean verificaContaReceberLegivel(ContaReceber contaReceber) {
        if (contaReceber.getOrdemServico() != null) {
            OrdemServico os = ordemServicoRepository.findById(contaReceber.getOrdemServico().getId()).get();
            os.getContaRecebers().sort(Comparator.comparing(ContaReceber::getNumeroParcela));
            for (ContaReceber cr : os.getContaRecebers()) {
                if (!cr.getNumeroParcela().equals(contaReceber.getNumeroParcela())) {
                    if (!cr.getStatus().equals(StatusConta.PAGO)) {
                        return true;
                    }
                } else {
                    if (contaReceber.getStatus().equals(StatusConta.PENDENTE)) {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 1; i <= contaReceber.getTotalParcelas(); i++) {
                if (i != contaReceber.getNumeroParcela()) {
                    if (!contaReceber.getStatus().equals(StatusConta.PAGO)) {
                        return true;
                    }
                } else {
                    if (contaReceber.getStatus().equals(StatusConta.PENDENTE)) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Transactional
    public void processarRecebimentoContasReceber(ContaReceber contaReceber) {
        Optional<ContaReceber> opContaReceber = contaReceberRepository.findById(contaReceber.getId());
        if (opContaReceber.isPresent()) {
            ContaReceber cr = opContaReceber.get();
            cr.setDataRecebimento(contaReceber.getDataRecebimento());
            cr.setValorRecebido(cr.getValor());
            cr.setTipoPagamento(contaReceber.getTipoPagamento());
            cr.setObservacao(contaReceber.getObservacao());
            cr.setStatus(StatusConta.PAGO);
            cr = contaReceberRepository.save(cr);
            if (cr.getTipoPagamento().equals(TipoPagamento.PIX) || cr.getTipoPagamento().equals(TipoPagamento.DINHEIRO)) {
                movimentaCaixaContaReceber(cr, TipoMovimentacao.ENTRADA);
            }
        }
    }

    @Transactional
    public void movimentaCaixaContaReceber(ContaReceber contaReceber, TipoMovimentacao tipoMovimentacao) {
        MovimentacaoCaixa mv = new MovimentacaoCaixa();
        Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
        mv.setCaixa(caixaAtual);
        mv.setContaReceber(contaReceber);
        mv.setTipo(tipoMovimentacao);
        mv.setDataMovimentacao(LocalDateTime.now());
        mv.setOrigemId(contaReceber.getId());
        if (contaReceber.getOrdemServico() != null) {
            mv.setValor(contaReceber.getValorRecebido() == null ? contaReceber.getValor() : contaReceber.getValorRecebido());
            PlanoDeContas contaContasReceber = planoDeContasRepository.findByCodigo("1.1.2.01")
                    .orElseThrow(() -> new RuntimeException("Conta 'Contas a Receber de Clientes' (1.1.2.01) não encontrada."));
            mv.setPlanoDeContas(contaContasReceber);
        } else {
            BigDecimal valorTotalJaPago = BigDecimal.ZERO;
            List<ContaReceber> todasAsParcelasDaVenda = contaReceberRepository.findByVendaAvulsaId(contaReceber.getVendaAvulsaId());
            for (ContaReceber parcela : todasAsParcelasDaVenda) {
                if (parcela.getStatus() == StatusConta.PAGO) {
                    valorTotalJaPago = valorTotalJaPago.add(parcela.getValorRecebido());
                }
            }
            mv.setValor(valorTotalJaPago);
            mv.setPlanoDeContas(contaReceber.getPlanoDeContas());
        }
        if (tipoMovimentacao.equals(TipoMovimentacao.ENTRADA)) {
            mv.setDescricao("Recebimento da Conta a Receber ID " + contaReceber.getId());
            mv.setOrigemTipo("PAGAMENTO_CONTA_RECEBER");
        } else if (tipoMovimentacao.equals(TipoMovimentacao.SAIDA)) {
            mv.setDescricao("Cancelamento da Conta a Receber ID " + contaReceber.getId());
            mv.setOrigemTipo("CANCELAMENTO_CONTA_RECEBER");
        }
        caixaAtual.getMovimentacoes().add(mv);
        caixaService.salvarCaixaAposMovimento(caixaAtual);
    }


    @Transactional
    public void criarContaReceberAvulsa(ContaReceber dadosDoFormulario) {
        LancamentoFinanceiro lancamentoReceita = new LancamentoFinanceiro(
                "Receita avulsa: " + (dadosDoFormulario.getObservacao() != null ? dadosDoFormulario.getObservacao() : dadosDoFormulario.getPlanoDeContas().getDescricao()),
                dadosDoFormulario.getValor(),
                LocalDate.now(),
                dadosDoFormulario.getPlanoDeContas(),
                null
        );
        lancamentoFinanceiroRepository.save(lancamentoReceita);

        List<ContaReceber> parcelasParaSalvar = new ArrayList<>();
        BigDecimal valorParcela = dadosDoFormulario.getValor().divide(BigDecimal.valueOf(dadosDoFormulario.getTotalParcelas()), RoundingMode.HALF_UP);
        BigDecimal valorCorrigido = BigDecimal.ZERO;
        for (int i = 1; i <= dadosDoFormulario.getTotalParcelas(); i++) {
            ContaReceber parcela = new ContaReceber();
            parcela.setCliente(dadosDoFormulario.getCliente());
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(dadosDoFormulario.getDataVencimento().plusMonths(i - 1));
            parcela.setStatus(StatusConta.PENDENTE);
            parcela.setValorRecebido(BigDecimal.ZERO);
            parcela.setNumeroParcela(i);
            parcela.setPlanoDeContas(dadosDoFormulario.getPlanoDeContas());
            parcela.setValorTotalOriginal(dadosDoFormulario.getValor());
            parcela.setTotalParcelas(dadosDoFormulario.getTotalParcelas());
            parcela.setValor(valorParcela);
            valorCorrigido = valorCorrigido.add(valorParcela);
            parcelasParaSalvar.add(parcela);
        }

        parcelasParaSalvar.getLast().setValor(parcelasParaSalvar.getLast().getValor().add(dadosDoFormulario.getValor().subtract(valorCorrigido)));
        List<ContaReceber> parcelasSalvas = contaReceberRepository.saveAll(parcelasParaSalvar);

        Long idDaVenda = parcelasSalvas.getFirst().getId();
        for (ContaReceber p : parcelasSalvas) {
            p.setVendaAvulsaId(idDaVenda);
        }
        contaReceberRepository.saveAll(parcelasSalvas);
    }

    @Transactional
    public void cancelarContaReceberAvulsa(Long umaParcelaId, String acaoFinanceira) {
        ContaReceber umaDasParcelas = contaReceberRepository.findById(umaParcelaId)
                .orElseThrow(() -> new RuntimeException("Conta a Receber com ID " + umaParcelaId + " não encontrada."));

        if (umaDasParcelas.getOrdemServico() != null || umaDasParcelas.getVendaAvulsaId() == null) {
            throw new IllegalStateException("Esta operação é válida apenas para Contas a Receber avulsas e parceladas.");
        }

        List<ContaReceber> todasAsParcelasDaVenda = contaReceberRepository.findByVendaAvulsaId(umaDasParcelas.getVendaAvulsaId());


        LancamentoFinanceiro estornoReceita = new LancamentoFinanceiro(
                "Estorno da Venda Avulsa (originada pela CR #" + umaParcelaId + ")",
                umaDasParcelas.getValorTotalOriginal().negate(),
                LocalDate.now(),
                umaDasParcelas.getPlanoDeContas(),
                null
        );
        lancamentoFinanceiroRepository.save(estornoReceita);


        if ("GERAR_CREDITO".equals(acaoFinanceira)) {
            BigDecimal valorTotalOriginalDaVenda = BigDecimal.ZERO;
            BigDecimal valorTotalJaPago = BigDecimal.ZERO;

            for (ContaReceber parcela : todasAsParcelasDaVenda) {
                valorTotalOriginalDaVenda = valorTotalOriginalDaVenda.add(parcela.getValor());
                if (parcela.getStatus() == StatusConta.PAGO) {
                    valorTotalJaPago = valorTotalJaPago.add(parcela.getValorRecebido());
                    parcela.setStatus(StatusConta.CANCELADO_CREDITO);
                } else {
                    parcela.setStatus(StatusConta.CANCELADO);
                }
            }
            contaReceberRepository.saveAll(todasAsParcelasDaVenda);
            if (valorTotalJaPago.compareTo(BigDecimal.ZERO) > 0) {
                Pessoa cliente = umaDasParcelas.getCliente();
                BigDecimal creditoAtual = cliente.getPesCredito() != null ? cliente.getPesCredito() : BigDecimal.ZERO;
                cliente.setPesCredito(creditoAtual.add(valorTotalJaPago));
                pessoaService.salvarEdit(cliente);
            }
        } else if ("REEMBOLSAR".equals(acaoFinanceira)) {
            movimentaCaixaContaReceberAvulsa(umaDasParcelas, TipoMovimentacao.SAIDA);
        }
    }


    @Transactional
    public void movimentaCaixaContaReceberAvulsa(ContaReceber contaReceber, TipoMovimentacao tipoMovimentacao) {
        MovimentacaoCaixa mv = new MovimentacaoCaixa();
        Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
        mv.setCaixa(caixaAtual);
        mv.setContaReceber(contaReceber);
        mv.setTipo(tipoMovimentacao);
        mv.setDataMovimentacao(LocalDateTime.now());
        mv.setOrigemId(contaReceber.getId());

        BigDecimal valorTotalJaPago = BigDecimal.ZERO;
        List<ContaReceber> todasAsParcelasDaVenda = contaReceberRepository.findByVendaAvulsaId(contaReceber.getVendaAvulsaId());
        for (ContaReceber parcela : todasAsParcelasDaVenda) {
            if (parcela.getStatus() == StatusConta.PAGO) {
                valorTotalJaPago = valorTotalJaPago.add(parcela.getValorRecebido());
                parcela.setStatus(StatusConta.CANCELADO_REEMBOLSO);
            } else {
                parcela.setStatus(StatusConta.CANCELADO);
            }
        }
        contaReceberRepository.saveAll(todasAsParcelasDaVenda);
        mv.setValor(valorTotalJaPago);
        mv.setPlanoDeContas(contaReceber.getPlanoDeContas());

        if (tipoMovimentacao.equals(TipoMovimentacao.SAIDA)) {
            mv.setDescricao("Cancelamento da Conta a Receber ID " + contaReceber.getId());
            mv.setOrigemTipo("CANCELAMENTO_CONTA_RECEBER");
        }
        caixaAtual.getMovimentacoes().add(mv);
        caixaService.salvarCaixaAposMovimento(caixaAtual);
    }

}
