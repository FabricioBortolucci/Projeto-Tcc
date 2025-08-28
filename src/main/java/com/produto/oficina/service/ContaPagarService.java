package com.produto.oficina.service;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.repository.CompraRepository;
import com.produto.oficina.repository.ContaPagarRepository;
import com.produto.oficina.repository.LancamentoFinanceiroRepository;
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
public class ContaPagarService {

    private final ContaPagarRepository contaPagarRepository;
    private final CompraRepository compraRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public ContaPagarService(ContaPagarRepository contaPagarRepository, CompraRepository compraRepository, CaixaService caixaService, PessoaService pessoaService, LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.contaPagarRepository = contaPagarRepository;
        this.compraRepository = compraRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
    }

    public void salvar(ContaPagar contaPagar) {
        contaPagarRepository.saveAndFlush(contaPagar);
    }

    public Page<ContaPagar> findAll(Pageable pageable) {
        return contaPagarRepository.findAll(pageable);
    }

    public List<ContaPagar> buscaContasByCompraId(Long id) {
        return contaPagarRepository.findAllByCompra_Id(id);
    }

    public ContaPagar findCpById(Long id) {
        return contaPagarRepository.findById(id).get();
    }

    public ContaPagar findCp(Long id) {
        ContaPagar cp = null;
        Optional<ContaPagar> contaPagar = contaPagarRepository.findById(id);
        if (contaPagar.isPresent()) {
            cp = new ContaPagar(contaPagar.get().getId(),
                    contaPagar.get().getValor(),
                    contaPagar.get().getDataVencimento(),
                    contaPagar.get().getDataPagamento(),
                    contaPagar.get().getNumeroParcela(),
                    contaPagar.get().getTotalParcelas(),
                    contaPagar.get().getValorTotalOriginal(),
                    contaPagar.get().getTipoPagamento(),
                    contaPagar.get().getStatus(),
                    contaPagar.get().getFornecedor(),
                    contaPagar.get().getCompra(),
                    contaPagar.get().getObservacao());
        }
        return cp;
    }

    @Transactional
    public void processarPagamentoContasPagar(ContaPagar contaPagar) {
        Optional<ContaPagar> opContaPagar = contaPagarRepository.findById(contaPagar.getId());
        if (opContaPagar.isPresent()) {
            ContaPagar cp = opContaPagar.get();
            cp.setDataPagamento(contaPagar.getDataPagamento());
            cp.setValorPago(contaPagar.getValorPago());
            cp.setTipoPagamento(contaPagar.getTipoPagamento());
            cp.setObservacao(contaPagar.getObservacao());
            cp.setStatus(StatusConta.PAGO);
            PlanoDeContas planoDeContasDaFatura = contaPagar.getPlanoDeContas();
            if (planoDeContasDaFatura.getNaturezaConta() == NaturezaContaPlanoContas.DESPESA ||
                    planoDeContasDaFatura.getNaturezaConta() == NaturezaContaPlanoContas.CUSTO) {

                LancamentoFinanceiro lancamentoDespesa = new LancamentoFinanceiro(
                        "Pagamento de despesa/custo: " + planoDeContasDaFatura.getDescricao(),
                        contaPagar.getValorPago(),
                        contaPagar.getDataPagamento(),
                        planoDeContasDaFatura,
                        null
                );
                lancamentoFinanceiroRepository.save(lancamentoDespesa);
            }
            cp = contaPagarRepository.save(cp);
            if (cp.getTipoPagamento().equals(TipoPagamento.PIX) || cp.getTipoPagamento().equals(TipoPagamento.DINHEIRO)) {
                movimentaCaixaContaPagar(cp, TipoMovimentacao.SAIDA, false);
            }
        }
    }

    public boolean verificaContaPagarLegivel(ContaPagar contaPagar) {
        Compra compra = compraRepository.findById(contaPagar.getCompra().getId()).get();
        compra.getContaPagars().sort(Comparator.comparing(ContaPagar::getNumeroParcela));
        for (ContaPagar cp : compra.getContaPagars()) {
            if (!cp.getNumeroParcela().equals(contaPagar.getNumeroParcela())) {
                if (!cp.getStatus().equals(StatusConta.PAGO)) {
                    return true;
                }
            } else {
                if (contaPagar.getStatus().equals(StatusConta.PENDENTE)) {
                    return false;
                }
            }
        }
        return false;
    }

    @Transactional
    public void movimentaCaixaContaPagar(ContaPagar contaPagar, TipoMovimentacao tipoMovimentacao, boolean avulsa) {
        MovimentacaoCaixa mv = new MovimentacaoCaixa();
        Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
        mv.setCaixa(caixaAtual);
        mv.setContaPagar(contaPagar);
        mv.setTipo(tipoMovimentacao);
        mv.setValor(contaPagar.getValorPago());
        mv.setDataMovimentacao(LocalDateTime.now());
        mv.setPlanoDeContas(contaPagar.getPlanoDeContas());
        mv.setOrigemId(contaPagar.getId());
        mv.setOrigemTipo("PAGAMENTO_CONTA_PAGAR");
        mv.setDescricao("Pagamento da Conta a Pagar ID " + contaPagar.getId());
        caixaAtual.getMovimentacoes().add(mv);
        caixaService.salvarCaixaAposMovimento(caixaAtual);
    }


    @Transactional
    public List<ContaPagar> criarContaPagarAvulsa(ContaPagar dadosDoFormulario) {
        if (dadosDoFormulario.getFornecedor() == null || dadosDoFormulario.getValor() == null ||
                dadosDoFormulario.getDataVencimento() == null || dadosDoFormulario.getPlanoDeContas() == null) {
            throw new IllegalArgumentException("Fornecedor, valor, vencimento e plano de contas são obrigatórios.");
        }
        NaturezaContaPlanoContas natureza = dadosDoFormulario.getPlanoDeContas().getNaturezaConta();
        if (natureza != NaturezaContaPlanoContas.DESPESA && natureza != NaturezaContaPlanoContas.CUSTO) {
            throw new IllegalArgumentException("A conta do plano de contas para uma C.P. avulsa deve ser de natureza DESPESA ou CUSTO.");
        }

        LancamentoFinanceiro lancamentoDespesa = new LancamentoFinanceiro(
                "Despesa avulsa: " + (dadosDoFormulario.getObservacao() != null ? dadosDoFormulario.getObservacao() : dadosDoFormulario.getPlanoDeContas().getDescricao()),
                dadosDoFormulario.getValor(),
                LocalDate.now(),
                dadosDoFormulario.getPlanoDeContas(),
                null
        );
        lancamentoFinanceiroRepository.save(lancamentoDespesa);

        List<ContaPagar> parcelasParaSalvar = new ArrayList<>();
        BigDecimal valorParcela = dadosDoFormulario.getValor().divide(BigDecimal.valueOf(dadosDoFormulario.getTotalParcelas()), RoundingMode.HALF_UP);

        for (int i = 1; i <= dadosDoFormulario.getTotalParcelas(); i++) {
            ContaPagar parcela = new ContaPagar();
            parcela.setFornecedor(dadosDoFormulario.getFornecedor());
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(dadosDoFormulario.getDataVencimento().plusMonths(i - 1));
            parcela.setStatus(StatusConta.PENDENTE);
            parcela.setValorPago(BigDecimal.ZERO);
            parcela.setNumeroParcela(i);
            parcela.setPlanoDeContas(dadosDoFormulario.getPlanoDeContas());
            parcela.setTotalParcelas(dadosDoFormulario.getTotalParcelas());
            parcela.setValorTotalOriginal(dadosDoFormulario.getValor());
            parcela.setObservacao(dadosDoFormulario.getObservacao());
            parcelasParaSalvar.add(parcela);
        }

        List<ContaPagar> parcelasSalvas = contaPagarRepository.saveAll(parcelasParaSalvar);

        Long idDaDespesa = parcelasSalvas.getFirst().getId();
        for (ContaPagar p : parcelasSalvas) {
            p.setDespesaAvulsaId(idDaDespesa);
        }
        return contaPagarRepository.saveAll(parcelasSalvas);
    }

    @Transactional
    public void cancelarContaPagarAvulsa(Long umaParcelaId) {
        ContaPagar umaDasParcelas = contaPagarRepository.findById(umaParcelaId)
                .orElseThrow(() -> new RuntimeException("Conta a Pagar com ID " + umaParcelaId + " não encontrada."));

        if (umaDasParcelas.getCompra() != null || umaDasParcelas.getDespesaAvulsaId() == null) {
            throw new IllegalStateException("Esta operação é válida apenas para Contas a Pagar avulsas.");
        }

        List<ContaPagar> todasAsParcelas = contaPagarRepository.findByDespesaAvulsaId(umaDasParcelas.getDespesaAvulsaId());

        BigDecimal valorTotalOriginalDaDespesa = BigDecimal.ZERO;
        BigDecimal valorTotalJaPago = BigDecimal.ZERO;

        for (ContaPagar parcela : todasAsParcelas) {
            valorTotalOriginalDaDespesa = valorTotalOriginalDaDespesa.add(parcela.getValor());
            if (parcela.getStatus() == StatusConta.PAGO) {
                valorTotalJaPago = valorTotalJaPago.add(parcela.getValorPago());
            }
            parcela.setStatus(StatusConta.CANCELADO);
        }
        contaPagarRepository.saveAll(todasAsParcelas);

        LancamentoFinanceiro estornoDespesa = new LancamentoFinanceiro(
                "Estorno da Despesa (originada pela CP #" + umaParcelaId + ")",
                valorTotalOriginalDaDespesa.negate(),
                LocalDate.now(),
                umaDasParcelas.getPlanoDeContas(),
                null
        );
        lancamentoFinanceiroRepository.save(estornoDespesa);

        Pessoa fornecedor = umaDasParcelas.getFornecedor();
        BigDecimal creditoAtual = fornecedor.getPesCredito() != null ? fornecedor.getPesCredito() : BigDecimal.ZERO;
        fornecedor.setPesCredito(creditoAtual.add(valorTotalJaPago));
        pessoaService.salvarEdit(fornecedor);
    }
}


