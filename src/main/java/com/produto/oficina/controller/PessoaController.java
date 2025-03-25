package com.produto.oficina.controller;

import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.service.PessoaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("novo_endereco", new EnderecoDto());
        model.addAttribute("novo_telefone", new TelefoneDto());
        return "pessoa/pesForm";
    }

    @PostMapping("/pessoa/cadastrar")
    private String pessoaCadastrar(@ModelAttribute PessoaDto pessoaDto) {
        System.out.println(pessoaDto.toString());
        return "redirect:/pessoa";
    }
}
