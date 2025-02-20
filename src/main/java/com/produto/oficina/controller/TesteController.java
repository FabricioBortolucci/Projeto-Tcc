package com.produto.oficina.controller;

import com.produto.oficina.entity.Teste;
import com.produto.oficina.service.TesteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("/teste")
public class TesteController {


    private final TesteService testeService;

    public TesteController(TesteService testeService) {
        this.testeService = testeService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Teste> nomes = testeService.listar();
        model.addAttribute("nomes", nomes);
        return "index";
    }


    @PostMapping("/salvar")
    public void salvarTeste(@RequestBody Teste t) {
        testeService.salvar(t);
    }
}
