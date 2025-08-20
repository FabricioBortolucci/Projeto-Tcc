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

            if (produto.getProdutoTipo().equals(ProdutoTipo.PECA)) {

                if (produto.getContaReceitaPadrao() == null || produto.getContaCusto() == null || produto.getContaEstoque() == null) {
                    throw new IllegalStateException("A peça '" + produto.getNome() +
                            "' não possui todas as contas financeiras (Receita, Custo, Estoque) configuradas.");
                }

                lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                        "Receita da peça '" + produto.getNome() + "' na OS #" + os.getId(),
                        item.getSubTotal(),
                        hoje,
                        produto.getContaReceitaPadrao(),
                        os
                ));

                BigDecimal custoTotalItem = produto.getPrecoCusto().multiply(item.getQuantidade());
                lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                        "Custo da peça '" + produto.getNome() + "' na OS #" + os.getId(), // Descrição correta
                        custoTotalItem,
                        hoje,
                        produto.getContaCusto(),
                        os
                ));

            } else if (produto.getProdutoTipo().equals(ProdutoTipo.MATERIA_PRIMA)) {

                if (produto.getContaCusto() == null || produto.getContaEstoque() == null) {
                    throw new IllegalStateException("A matéria-prima '" + produto.getNome() +
                            "' não possui as contas de Custo e/ou Estoque configuradas.");
                }

                BigDecimal custoTotalItem = produto.getPrecoCusto().multiply(item.getQuantidade());
                lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                        "Custo da matéria-prima '" + produto.getNome() + "' na OS #" + os.getId(),
                        custoTotalItem,
                        hoje,
                        produto.getContaCusto(),
                        os
                ));

            } else {
                throw new IllegalStateException("O produto '" + produto.getNome() + "' está com um tipo inválido ou não definido.");
            }
        }

        for (OrdemServicoItem servicoPrestado : os.getItensServico()) {
            Servico servico = servicoRepository.findById(servicoPrestado.getServico().getId()).get();
            if (servico.getContaReceitaPadrao() == null || servico.getContaCusto() == null) {
                throw new IllegalStateException("O serviço '" + servico.getDescricao() + "' não possui Conta de Receita e/ou Custo configurada.");
            }

            lancamentoFinanceiroRepository.save(new LancamentoFinanceiro(
                    "Receita do serviço '" + servico.getDescricao() + "' na OS #" + os.getId(),
                    servicoPrestado.getValorUnitario(),
                    hoje,
                    servico.getContaReceitaPadrao(),
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
    public void cancelarOrdemDeServico(OrdemServico ordemServico, List<Long> idsDasPecasParaDevolver, String acaoFinanceira) {
        if (ordemServico.getStatus().equals(StatusOS.FINALIZADA)) {
            BigDecimal valorTotalCreditoCliente = BigDecimal.ZERO;
            ordemServico.setContaRecebers(contaReceberService.findAllByOsId(ordemServico.getId()));

            List<ContaReceber> crCancels = new ArrayList<>();
            for (ContaReceber contaReceber : ordemServico.getContaRecebers()) {
                if (contaReceber.getStatus().equals(StatusConta.PENDENTE)) {
                    contaReceber.setStatus(StatusConta.CANCELADO);
                    crCancels.add(contaReceber);
                }
                if (contaReceber.getStatus().equals(StatusConta.PAGO)) {
                    if (acaoFinanceira.equals("GERAR_CREDITO")) {
                        valorTotalCreditoCliente = valorTotalCreditoCliente.add(contaReceber.getValor());
                        contaReceber.setStatus(StatusConta.CANCELADO_CREDITO);
                    }
                    if (acaoFinanceira.equals("REEMBOLSAR")) {
                        contaReceber.setStatus(StatusConta.CANCELADO_REEMBOLSO);
                    }
                }
            }

            if (idsDasPecasParaDevolver != null && !idsDasPecasParaDevolver.isEmpty()) {
                for (Long id : idsDasPecasParaDevolver) {
                    for (OrdemServicoPeca pecasDevolucao : ordemServico.getPecasUsadas()) {
                        if (pecasDevolucao.getProduto().getId().equals(id)) {
                            produtoRepository.findById(pecasDevolucao.getProduto().getId()).ifPresent(produto -> {
                                produto.setEstoque(produto.getEstoque() + pecasDevolucao.getQuantidade().intValue());
                                produtoRepository.save(produto);

                                MovimentacaoEstoque movEstoque = new MovimentacaoEstoque();
                                movEstoque.setProduto(produto);
                                movEstoque.setCustoUnitario(produto.getPrecoUnitario());
                                movEstoque.setUsuarioResponsavel(pessoaService.buscaPessoaLogada());
                                movEstoque.setQuantidade(pecasDevolucao.getQuantidade());
                                movEstoque.setTipo(TipoMovimentacao.ENTRADA);
                                movEstoque.setDataMovimentacao(LocalDateTime.now());
                                movEstoque.setOrigemId(ordemServico.getId());
                                movEstoque.setOrigemTipo("ORDEM_DE_SERVICO");
                                movEstoque.setObservacao("Entrada de material da OS nº " + ordemServico.getId());
                                movimentacaoEstoqueRepository.save(movEstoque);
                            });
                        }
                    }
                }
            }

            if (ordemServico.getPlanoPagamento().equals(PlanoPagamento.AVISTA) &&
                    acaoFinanceira.equals("REEMBOLSAR") && crCancels.isEmpty()) {
                Caixa caixaAtual = caixaService.buscaCaixaAtualAberto();

                MovimentacaoCaixa novoMovimento = new MovimentacaoCaixa();
                novoMovimento.setCaixa(caixaAtual);
                novoMovimento.setDataMovimentacao(LocalDateTime.now());
                novoMovimento.setTipo(TipoMovimentacao.SAIDA);
                novoMovimento.setContaReceber(ordemServico.getContaRecebers().getFirst());
                novoMovimento.setValor(ordemServico.getValorTotal());
                novoMovimento.setDescricao("Reembolso da OS nº " + ordemServico.getId() + " - Cliente: " + ordemServico.getCliente().getPesNome());

                caixaAtual.getMovimentacoes().add(novoMovimento);
                caixaService.salvarCaixaAposMovimento(caixaAtual);
            }

            if (acaoFinanceira.equals("GERAR_CREDITO")) {
                ordemServico.getCliente().setPesCredito(ordemServico.getCliente().getPesCredito().add(valorTotalCreditoCliente));
                pessoaRepository.save(ordemServico.getCliente());
            }

            ordemServico.setUsuarioCancelou(pessoaService.buscaUsuarioLogado());
            ordemServico.setDataCancelamento(LocalDateTime.now());
            ordemServico.setStatus(StatusOS.CANCELADA);
            ordemServicoRepository.save(ordemServico);
        }
    }
}

