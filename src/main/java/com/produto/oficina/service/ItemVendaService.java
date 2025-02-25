package com.produto.oficina.service;

import com.produto.oficina.repository.ItemVendaRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemVendaService {


    private final ItemVendaRepository itemVendaRepository;

    public ItemVendaService(ItemVendaRepository itemVendaRepository) {
        this.itemVendaRepository = itemVendaRepository;
    }
}
