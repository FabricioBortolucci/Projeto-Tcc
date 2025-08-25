package com.produto.oficina.service;

import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.*;
import com.produto.oficina.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;
    private final ProdutoRepository produtoRepository;
    private final PessoaRepository pessoaRepository;
    private final CaixaService caixaService;
    private final ContaReceberService contaReceberService;
    private final PessoaService pessoaService;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, ServicoRepository servicoRepository, ProdutoRepository produtoRepository, PessoaRepository pessoaRepository, CaixaService caixaService, ContaReceberService contaReceberService, PessoaService pessoaService, MovimentacaoEstoqueRepository movimentacaoEstoqueRepository, LancamentoFinanceiroRepository lancamentoFinanceiroRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.servicoRepository = servicoRepository;
        this.produtoRepository = produtoRepository;
        this.pessoaRepository = pessoaRepository;
        this.caixaService = caixaService;
        this.contaReceberService = contaReceberService;
        this.pessoaService = pessoaService;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.lancamentoFinanceiroRepository = lancamentoFinanceiroRepository;
    }

    public Page<OrdemServico> findAll(Pageable pageable) {
        return ordemServicoRepository.findAll(pageable);
    }

    public void adicionaServicoLista(BigDecimal valorServico, Integer qtdServico, OrdemServico ordemServico, Long idServico) {
        Servico serv = servicoRepository.findById(idServico).get();

        for (OrdemServicoItem item : ordemServico.getItensServico()) {
            if (item.getServico().getId().equals(serv.getId())) {
                if (item.getValorUnitario().equals(valorServico)) {
                    item.setQuantidade(item.getQuantidade() + qtdServico);
                    item.setValorTotal(item.getSubTotal());
                    return;
                }
            }
        }

        OrdemServicoItem ordemServicoItem = new OrdemServicoItem();
        ordemServicoItem.setServico(serv);
        ordemServicoItem.setQuantidade(qtdServico);
        ordemServicoItem.setValorUnitario(valorServico);
        ordemServicoItem.setOrdemServico(ordemServico);
        ordemServico.getItensServico().add(ordemServicoItem);
    }

    public void adicionaProdutoLista(BigDecimal valorProd, Integer qtdProd, OrdemServico ordemServico, Long idProd) {
        Produto prod = produtoRepository.findById(idProd).get();

        for (OrdemServicoPeca item : ordemServico.getPecasUsadas()) {
            if (item.getProduto().getId().equals(prod.getId())) {
                if (item.getValorUnitario().equals(valorProd)) {
                    item.setQuantidade(item.getQuantidade().add(BigDecimal.valueOf(qtdProd)));
                    item.setValorTotal(item.getSubTotal());
                    return;
                }
            }
        }

        OrdemServicoPeca ordemServicoPeca = new OrdemServicoPeca();
        ordemServicoPeca.setProduto(prod);
        ordemServicoPeca.setQuantidade(BigDecimal.valueOf(qtdProd));
        ordemServicoPeca.setValorUnitario(valorProd);
        ordemServicoPeca.setOrdemServico(ordemServico);
        ordemServico.getPecasUsadas().add(ordemServicoPeca);
    }

    @Transactional
    public void salvarRascunho(OrdemServico ordemServico) {
        if (ordemServico.getCliente().getId() != null && ordemServico.getFuncionario().getId() != null) {
            ordemServico.setCliente(pessoaRepository.findById(ordemServico.getCliente().getId()).get());
            ordemServico.setFuncionario(pessoaRepository.findById(ordemServico.getFuncionario().getId()).get());
        } else {
            ordemServico.setCliente(null);
            ordemServico.setFuncionario(null);
        }

        ordemServico.setValorTotal(ordemServico.getCalculaTotalServicoItens().add(ordemServico.getCalculaTotalProdsItens()));
        if (ordemServico.getStatus() == null) ordemServico.setStatus(StatusOS.ABERTA);
        ordemServicoRepository.saveAndFlush(ordemServico);
    }

    public void cancelarOS(Long id) {
        ordemServicoRepository.deleteById(id);
        ordemServicoRepository.flush();
    }

    public OrdemServico findById(Long id) {
        return ordemServicoRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public OrdemServico preparaFinalizacaoView(Long id) {
        OrdemServico os = ordemServicoRepository.findByIdWithItensServico(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada!"));

        ordemServicoRepository.findByIdWithPecasUsadas(id);

        ordemServicoRepository.findByIdWithContasReceber(id);

        return os;
    }

    @Transactional
    public OrdemServico preparaFinalizacao(OrdemServico ordemServico) {
        if (ordemServico.getCliente().getId() != null && ordemServico.getFuncionario().getId() != null) {
            ordemServico.setCliente(pessoaRepository.findById(ordemServico.getCliente().getId()).get());
            ordemServico.setFuncionario(pessoaRepository.findById(ordemServico.getFuncionario().getId()).get());
            ordemServico.setValorTotal(ordemServico.getCalculaTotalServicoItens().add(ordemServico.getCalculaTotalProdsItens()));
        }
        for (OrdemServicoItem item : ordemServico.getItensServico()) {
            item.setValorTotal(item.getSubTotal());
        }
        for (OrdemServicoPeca item : ordemServico.getPecasUsadas()) {
            item.setValorTotal(item.getSubTotal());
        }
        ordemServico.setStatus(StatusOS.ABERTA);
        ordemServicoRepository.save(ordemServico);
        return ordemServico;
    }


    @Transactional
    public void finalizarOs(OrdemServico os) {
        LocalDate hoje = LocalDate.now();

        for (OrdemServicoPeca item : os.getPecasUsadas()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalStateException("Produto com ID " + item.getProduto().getId() + " não encontrado."));

            if (produto.getContaReceitaPadrao() == null || produto.getContaCusto() == null || produto.getContaEstoque() == null) {
                throw new IllegalStateException("O produto '" + produto.getNome() + "' não possui todas as contas financeiras necessárias configuradas.");
            }

            lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                    "Receita da venda de '" + produto.getNome() + "' na OS #" + os.getId(),
                    item.getSubTotal(),
                    hoje,
                    produto.getContaReceitaPadrao(),
                    os
            ));


            BigDecimal custoTotalItem = produto.getPrecoCusto().multiply(item.getQuantidade());
            lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                    "Custo do(a) '" + produto.getNome() + "' na OS #" + os.getId(),
                    custoTotalItem,
                    hoje,
                    produto.getContaCusto(),
                    os
            ));
        }

        for (OrdemServicoItem servicoPrestado : os.getItensServico()) {
            Servico servico = servicoRepository.findById(servicoPrestado.getServico().getId())
                    .orElseThrow(() -> new IllegalStateException("Serviço com ID " + servicoPrestado.getServico().getId() + " não encontrado."));

            if (servico.getContaReceitaPadrao() == null || servico.getContaCusto() == null) {
                throw new IllegalStateException("O serviço '" + servico.getDescricao() + "' não possui Conta de Receita e/ou Custo configurada.");
            }

            lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                    "Receita do serviço '" + servico.getDescricao() + "' na OS #" + os.getId(),
                    servicoPrestado.getSubTotal(),
                    hoje,
                    servico.getContaReceitaPadrao(),
                    os
            ));

            BigDecimal custoTotalServico = servico.getPrecoCusto() != null ? servico.getPrecoCusto() : BigDecimal.ZERO;
            lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                    "Custo do serviço '" + servico.getDescricao() + "' na OS #" + os.getId(),
                    custoTotalServico,
                    hoje,
                    servico.getContaCusto(),
                    os
            ));
        }

        List<ContaReceber> contasReceber = new ArrayList<>();

        if (os.getPlanoPagamento().equals(PlanoPagamento.APRAZO)) {
            BigDecimal valorParcela = os.getValorTotal().divide(BigDecimal.valueOf(os.getQuantParcelas()), RoundingMode.HALF_UP);
            for (int i = 1; i <= os.getQuantParcelas(); i++) {
                ContaReceber cr = new ContaReceber();
                cr.setOrdemServico(os);
                cr.setCliente(os.getCliente());
                cr.setValor(valorParcela);
                cr.setValorTotalOriginal(os.getValorTotal());
                cr.setStatus(StatusConta.PENDENTE);
                cr.setNumeroParcela(i);
                cr.setTotalParcelas(os.getQuantParcelas());
                LocalDate dataVencimento = hoje.plusDays(os.getIntDias()).plusMonths(i - 1);
                cr.setDataVencimento(dataVencimento);
                contasReceber.add(cr);
            }
        } else {
            ContaReceber cr = new ContaReceber();
            cr.setOrdemServico(os);
            cr.setCliente(os.getCliente());
            cr.setValor(os.getValorTotal());
            cr.setValorTotalOriginal(os.getValorTotal());
            cr.setValorRecebido(os.getValorTotal());
            cr.setStatus(StatusConta.PAGO);
            cr.setNumeroParcela(1);
            cr.setTotalParcelas(os.getQuantParcelas());
            cr.setDataRecebimento(LocalDate.now());
            cr.setTipoPagamento(os.getTipoPagamento());
            cr.setDataVencimento(os.getDataAbertura().toLocalDate().plusDays(os.getIntDias()));
            cr.setDataRecebimento(LocalDate.now());
            contasReceber.add(cr);
        }
        os.setContaRecebers(contasReceber);

        for (OrdemServicoPeca item : os.getPecasUsadas()) {
            Produto prod = item.getProduto();
            if (prod.getEstoque() < item.getQuantidade().intValue()) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + prod.getNome());
            }
            prod.setEstoque(prod.getEstoque() - item.getQuantidade().intValue());
            produtoRepository.save(prod);

            MovimentacaoEstoque movEstoque = new MovimentacaoEstoque(
                    prod,
                    item.getQuantidade(),
                    TipoMovimentacao.SAIDA,
                    prod.getPrecoCusto(),
                    prod.getContaEstoque(),
                    prod.getContaCusto(),
                    os.getId(),
                    "ORDEM_DE_SERVICO",
                    pessoaService.buscaPessoaLogada(),
                    "Saída de material para a OS #" + os.getId()
            );
            movimentacaoEstoqueRepository.save(movEstoque);
        }

        os.setStatus(StatusOS.FINALIZADA);
        os.setDataFechamento(LocalDateTime.now());
        OrdemServico osFinalizada = ordemServicoRepository.save(os);

        if (osFinalizada.getPlanoPagamento().equals(PlanoPagamento.AVISTA)) {
            Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
            MovimentacaoCaixa novoMovimento = new MovimentacaoCaixa();
            novoMovimento.setCaixa(caixaAtual);
            novoMovimento.setTipo(TipoMovimentacao.ENTRADA);
            novoMovimento.setValor(osFinalizada.getValorTotal());
            novoMovimento.setContaReceber(osFinalizada.getContaRecebers().getFirst());
            novoMovimento.setDescricao("Recebimento da OS #" + osFinalizada.getId() + " - Cliente: " + osFinalizada.getCliente().getPesNome());
            novoMovimento.setDataMovimentacao(LocalDateTime.now());
            caixaService.salvarCaixaAposMovimento(caixaAtual, novoMovimento);
        }
    }

    public void executarOs(Long id) {
        OrdemServico os = ordemServicoRepository.findById(id).get();
        os.setStatus(StatusOS.EM_EXECUCAO);
        ordemServicoRepository.save(os);
    }

    public long countByStatusAbertaEexecucao() {
        long num = ordemServicoRepository.countByStatus(StatusOS.ABERTA);
        num += ordemServicoRepository.countByStatus(StatusOS.EM_EXECUCAO);
        return num;
    }

    public BigDecimal buscaFaturamentoMensal() {
        LocalDate hoje = LocalDate.now();

        // Calcula o primeiro dia do mês atual à meia-noite (início do dia)
        LocalDateTime inicioDoMes = hoje.withDayOfMonth(1).atStartOfDay();

        // Calcula o último dia do mês atual às 23:59:59.999... (fim do dia)
        LocalDateTime fimDoMes = hoje.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        BigDecimal valorTotalOsMes = BigDecimal.ZERO;
        List<OrdemServico> os = ordemServicoRepository.findAllByDataFechamentoBetween(inicioDoMes, fimDoMes);
        for (OrdemServico o : os) {
            valorTotalOsMes = valorTotalOsMes.add(o.getValorTotal());
        }
        return valorTotalOsMes;

    }

    public OrdemServico save(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }

    @Transactional
    public void cancelarOrdemDeServico(OrdemServico os, List<Long> idsPecasDevolucao, String acaoFinanceira) {
        if (!os.getStatus().equals(StatusOS.FINALIZADA)) {
            throw new IllegalStateException("Apenas Ordens de Serviço finalizadas podem ser canceladas.");
        }

        os.setContaRecebers(contaReceberService.findAllByOsId(os.getId()));

        List<LancamentoFinanceiro> lancamentosOriginais = lancamentoFinanceiroRepository.findByOrdemServico(os);
        for (LancamentoFinanceiro lancamento : lancamentosOriginais) {
            LancamentoFinanceiro estorno = new LancamentoFinanceiro(
                    "Estorno: " + lancamento.getDescricao(),
                    lancamento.getValor().negate(),
                    LocalDate.now(),
                    lancamento.getPlanoDeContas(),
                    os
            );
            lancamentoFinanceiroRepository.save(estorno);
        }

        if (idsPecasDevolucao != null && !idsPecasDevolucao.isEmpty()) {
            for (Long id : idsPecasDevolucao) {
                for (OrdemServicoPeca pecaUsada : os.getPecasUsadas()) {
                    if (id.equals(pecaUsada.getProduto().getId())) {
                        Produto produto = pecaUsada.getProduto();
                        produto.setEstoque(produto.getEstoque() + pecaUsada.getQuantidade().intValue());
                        produtoRepository.save(produto);

                        MovimentacaoEstoque movEstoque = new MovimentacaoEstoque(
                                produto,
                                pecaUsada.getQuantidade(),
                                TipoMovimentacao.ENTRADA,
                                produto.getPrecoCusto(),
                                produto.getContaEstoque(),
                                produto.getContaCusto(),
                                os.getId(),
                                "CANCELAMENTO_OS",
                                pessoaService.buscaPessoaLogada(),
                                "Entrada por cancelamento da OS #" + os.getId()
                        );
                        movimentacaoEstoqueRepository.save(movEstoque);
                    }
                }
            }
        }

        BigDecimal valorTotalPagoPeloCliente = BigDecimal.ZERO;

        for (ContaReceber contaReceber : os.getContaRecebers()) {
            if (contaReceber.getStatus().equals(StatusConta.PAGO)) {
                valorTotalPagoPeloCliente = valorTotalPagoPeloCliente.add(contaReceber.getValorRecebido());
                if ("GERAR_CREDITO".equals(acaoFinanceira)) {
                    contaReceber.setStatus(StatusConta.CANCELADO_CREDITO);
                } else if ("REEMBOLSAR".equals(acaoFinanceira)) {
                    contaReceber.setStatus(StatusConta.CANCELADO_REEMBOLSO);
                }
            } else {
                contaReceber.setStatus(StatusConta.CANCELADO);
            }
        }

        if (valorTotalPagoPeloCliente.compareTo(BigDecimal.ZERO) > 0) {
            if ("GERAR_CREDITO".equals(acaoFinanceira)) {
                Pessoa cliente = os.getCliente();
                BigDecimal creditoAtual = cliente.getPesCredito() != null ? cliente.getPesCredito() : BigDecimal.ZERO;
                cliente.setPesCredito(creditoAtual.add(valorTotalPagoPeloCliente));
                pessoaRepository.save(cliente);
            } else if ("REEMBOLSAR".equals(acaoFinanceira)) {
                Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();
                MovimentacaoCaixa movimentoReembolso = new MovimentacaoCaixa();
                movimentoReembolso.setCaixa(caixaAtual);
                movimentoReembolso.setDataMovimentacao(LocalDateTime.now());
                movimentoReembolso.setTipo(TipoMovimentacao.SAIDA);
                movimentoReembolso.setValor(valorTotalPagoPeloCliente);
                movimentoReembolso.setDescricao("Reembolso da OS #" + os.getId() + " - Cliente: " + os.getCliente().getPesNome());
                caixaService.salvarCaixaAposMovimento(caixaAtual, movimentoReembolso);
            }
        }

        os.setStatus(StatusOS.CANCELADA);
        os.setUsuarioCancelou(pessoaService.buscaUsuarioLogado());
        os.setDataCancelamento(LocalDateTime.now());
        ordemServicoRepository.save(os);
    }
}

