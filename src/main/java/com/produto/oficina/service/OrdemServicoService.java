package com.produto.oficina.service;

import com.produto.oficina.model.*;
import com.produto.oficina.model.enums.StatusOS;
import com.produto.oficina.repository.OrdemServicoRepository;
import com.produto.oficina.repository.ProdutoRepository;
import com.produto.oficina.repository.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;
    private final ProdutoRepository produtoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, ServicoRepository servicoRepository, ProdutoRepository produtoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.servicoRepository = servicoRepository;
        this.produtoRepository = produtoRepository;
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

    public void salvarRascunho(OrdemServico ordemServico) {
        ordemServico.setStatus(StatusOS.ABERTA);
        ordemServicoRepository.save(ordemServico);
    }

    public void cancelarOS(Long id) {
        ordemServicoRepository.deleteById(id);
        ordemServicoRepository.flush();
    }
}

