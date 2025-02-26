package com.produto.oficina.controller;

import com.produto.oficina.model.Cidade;
import com.produto.oficina.model.Teste;
import com.produto.oficina.service.CidadeService;
import com.produto.oficina.service.TesteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller()
public class TesteController {


    private final TesteService testeService;
    private final CidadeService cidadeService;

    public TesteController(TesteService testeService, CidadeService cidadeService) {
        this.cidadeService = cidadeService;
        this.testeService = testeService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        List<Teste> nomes = testeService.listar();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("nomes", nomes);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        List<Cidade> nomes = cidadeService.listar();
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("cids", nomes);
        return modelAndView;
    }

    @PostMapping("/salvar")
    public void salvarTeste(@RequestBody Teste t) {
        testeService.salvar(t);
    }
}
