package com.produto.oficina.service;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.repository.CompraRepository;
import com.produto.oficina.repository.ContaPagarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ContaPagarService {

    private final ContaPagarRepository contaPagarRepository;
    private final CompraRepository compraRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;

    public ContaPagarService(ContaPagarRepository contaPagarRepository, CompraRepository compraRepository, CaixaService caixaService, PessoaService pessoaService) {
        this.contaPagarRepository = contaPagarRepository;
        this.compraRepository = compraRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
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
            cp = contaPagarRepository.save(cp);
            if (cp.getTipoPagamento().equals(TipoPagamento.PIX) || cp.getTipoPagamento().equals(TipoPagamento.DINHEIRO)) {
                movimentaCaixaContaPagar(cp);
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
    public void movimentaCaixaContaPagar(ContaPagar contaPagar) {
        MovimentacaoCaixa mv = new MovimentacaoCaixa();
        Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
        mv.setCaixa(caixaAtual);
        mv.setContaPagar(contaPagar);
        mv.setTipo(TipoMovimentacao.SAIDA);
        mv.setValor(contaPagar.getValorPago());
        mv.setDataMovimentacao(LocalDateTime.now());
        mv.setPlanoDeContas(contaPagar.getPlanoDeContas());
        mv.setOrigemId(contaPagar.getId());
        mv.setOrigemTipo("PAGAMENTO_CONTA_PAGAR");
        mv.setDescricao("Pagamento da Conta a Pagar ID " + contaPagar.getId() + ", Fornecedor " + contaPagar.getFornecedor().getPesNome());
        caixaAtual.getMovimentacoes().add(mv);
        caixaService.salvarCaixaAposMovimento(caixaAtual);
    }

    @Transactional
    public void cancelarContaPagar(Long id) {
        ContaPagar contaPagar = contaPagarRepository.findById(id).get();
        if (contaPagar.getStatus().equals(StatusConta.PENDENTE)) {
            contaPagar.setStatus(StatusConta.CANCELADO);
            String obsOriginalPendente = contaPagar.getObservacao() != null ? contaPagar.getObservacao() : "";
            contaPagar.setObservacao(obsOriginalPendente + " [Cancelada em " +
                    JavaUtils.formatLocalDate(LocalDate.now()) + " por " +
                    pessoaService.buscaUsuarioLogado().getUsuNome() + "]");

        } else if (contaPagar.getStatus().equals(StatusConta.PAGO)) {
            Pessoa fornecedor = pessoaService.buscaFornecedorPorId(contaPagar.getFornecedor().getId()).get();
            BigDecimal valorEstornar = contaPagar.getValorPago() != null ? contaPagar.getValorPago() : BigDecimal.ZERO;
            BigDecimal valorCredito = fornecedor.getPesCredito() != null ? fornecedor.getPesCredito() : BigDecimal.ZERO;
            fornecedor.setPesCredito(valorCredito.add(valorEstornar));

            String observacaoOriginal = contaPagar.getObservacao() != null ? contaPagar.getObservacao() : "";
            String detalheEstorno = " [Pagamento de " +
                    (contaPagar.getDataPagamento() != null ? JavaUtils.formatLocalDate(contaPagar.getDataPagamento()) : "data desconhecida") +
                    " no valor de " + JavaUtils.formatMonetaryString(valorEstornar) +
                    " estornado em " + JavaUtils.formatLocalDate(LocalDate.now()) +
                    " por usuário " + pessoaService.buscaUsuarioLogado().getUsuNome() +
                    ". Valor convertido em crédito com fornecedor.]";

            contaPagar.setObservacao(observacaoOriginal.isEmpty() ? detalheEstorno.trim() : observacaoOriginal + detalheEstorno);

            contaPagar.setStatus(StatusConta.PENDENTE);
            contaPagar.setDataPagamento(null);
            contaPagar.setTipoPagamento(null);
            contaPagar.setValorPago(BigDecimal.ZERO);

            pessoaService.salvarEdit(fornecedor);
        }
        contaPagarRepository.save(contaPagar);
    }
}
