package com.produto.oficina.service;

import com.produto.oficina.model.OrdemServico;
import com.produto.oficina.model.OrdemServicoItem;
import com.produto.oficina.model.Servico;
import com.produto.oficina.repository.OrdemServicoRepository;
import com.produto.oficina.repository.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository, ServicoRepository servicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.servicoRepository = servicoRepository;
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
}

