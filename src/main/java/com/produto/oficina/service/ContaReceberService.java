package com.produto.oficina.service;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.StatusConta;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.model.enums.TipoPagamento;
import com.produto.oficina.repository.ContaReceberRepository;
import com.produto.oficina.repository.OrdemServicoRepository;
import com.produto.oficina.repository.PlanoDeContasRepository;
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
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final CaixaService caixaService;
    private final PessoaService pessoaService;
    private final PlanoDeContasRepository planoDeContasRepository;

    public ContaReceberService(ContaReceberRepository contaReceberRepository, OrdemServicoRepository ordemServicoRepository, CaixaService caixaService, PessoaService pessoaService, PlanoDeContasRepository planoDeContasRepository) {
        this.contaReceberRepository = contaReceberRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.caixaService = caixaService;
        this.pessoaService = pessoaService;
        this.planoDeContasRepository = planoDeContasRepository;
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
        mv.setValor(contaReceber.getValorRecebido() == null ? contaReceber.getValor() : contaReceber.getValorRecebido());
        mv.setDataMovimentacao(LocalDateTime.now());
        mv.setOrigemId(contaReceber.getId());
        mv.setOrigemTipo("PAGAMENTO_CONTA_RECEBER");
        if (contaReceber.getOrdemServico() != null) {
            PlanoDeContas contaContasReceber = planoDeContasRepository.findByCodigo("3.01.03")
                    .orElseThrow(() -> new RuntimeException("Conta 'Contas a Receber de Clientes' (3.01.03) não encontrada."));

            mv.setPlanoDeContas(contaContasReceber);
        } else {
            mv.setPlanoDeContas(contaReceber.getPlanoDeContas());
        }

        if (tipoMovimentacao.equals(TipoMovimentacao.ENTRADA)) {
            mv.setDescricao("Recebimento da Conta a Receber ID " + contaReceber.getId() + ", Cliente: " + contaReceber.getCliente().getPesNome());
        } else if (tipoMovimentacao.equals(TipoMovimentacao.SAIDA)) {
            mv.setDescricao("Cancelamento da Conta a Receber ID " + contaReceber.getId() + ", Cliente: " + contaReceber.getCliente().getPesNome());
        }
        caixaAtual.getMovimentacoes().add(mv);
        caixaService.salvarCaixaAposMovimento(caixaAtual);
    }

    @Transactional
    public void cancelarContaReceber(Long id) {
        ContaReceber contaReceber = contaReceberRepository.findById(id).get();
        OrdemServico ordemServico = ordemServicoRepository.findById(contaReceber.getOrdemServico().getId()).get();
        if (contaReceber.getStatus().equals(StatusConta.PENDENTE)) {
            contaReceber.setStatus(StatusConta.CANCELADO);
            String obsOriginalPendente = contaReceber.getObservacao() != null ? contaReceber.getObservacao() : "";
            contaReceber.setObservacao(obsOriginalPendente + " [Cancelada em " +
                    JavaUtils.formatLocalDate(LocalDate.now()) + " por " +
                    pessoaService.buscaUsuarioLogado().getUsuNome() + "]");

        } else if (contaReceber.getStatus().equals(StatusConta.PAGO)) {
            if (contaReceber.getTipoPagamento().equals(TipoPagamento.DINHEIRO) ||
                    contaReceber.getTipoPagamento().equals(TipoPagamento.PIX) ||
                    contaReceber.getTipoPagamento().equals(TipoPagamento.BOLETO)) {
                movimentaCaixaContaReceber(contaReceber, TipoMovimentacao.SAIDA);
            } else {
                Pessoa cliente = pessoaService.buscaClientePorId(contaReceber.getCliente().getId());
                BigDecimal valorEstornar = contaReceber.getValorRecebido() != null ? contaReceber.getValorRecebido() : BigDecimal.ZERO;
                BigDecimal valorCredito = cliente.getPesCredito() != null ? cliente.getPesCredito() : BigDecimal.ZERO;
                cliente.setPesCredito(valorCredito.add(valorEstornar));

                String observacaoOriginal = contaReceber.getObservacao() != null ? contaReceber.getObservacao() : "";
                String detalheEstorno = " [Recebimento de " +
                        (contaReceber.getDataRecebimento() != null ? JavaUtils.formatLocalDate(contaReceber.getDataRecebimento()) : "data desconhecida") +
                        " no valor de " + JavaUtils.formatMonetaryString(valorEstornar) +
                        " reembolsado em " + JavaUtils.formatLocalDate(LocalDate.now()) +
                        " por usuário " + pessoaService.buscaUsuarioLogado().getUsuNome() +
                        ". Valor convertido em crédito com fornecedor.]";
                contaReceber.setObservacao(observacaoOriginal.isEmpty() ? detalheEstorno.trim() : observacaoOriginal + detalheEstorno);

                pessoaService.salvarEdit(cliente);
            }
            contaReceber.setStatus(StatusConta.PENDENTE);
            contaReceber.setDataRecebimento(null);
            contaReceber.setTipoPagamento(null);
            contaReceber.setValorRecebido(BigDecimal.ZERO);
        }
        contaReceberRepository.save(contaReceber);
    }
}
