package com.produto.oficina.controller;

import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.service.PessoaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PessoaController {


    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping("/pessoa")
    private String pessoaList(Model model) {
        model.addAttribute("pes_list", pessoaService.listarTodos());
        return "pessoa/pesList";
    }

    @GetMapping("/pessoa/cadastro")
    private String pessoaCadastro(Model model) {
        model.addAttribute("novo_pessoa", new PessoaDto());
        return "pessoa/pesForm";
    }
}
