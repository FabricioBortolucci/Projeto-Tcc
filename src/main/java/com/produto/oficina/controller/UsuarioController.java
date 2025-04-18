package com.produto.oficina.controller;

import com.produto.oficina.dto.UsuarioDTO;
import com.produto.oficina.model.enums.Role;
import com.produto.oficina.service.PessoaService;
import com.produto.oficina.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final PessoaService pessoaService;

    public UsuarioController(UsuarioService usuarioService, PessoaService pessoaService) {
        this.usuarioService = usuarioService;
        this.pessoaService = pessoaService;
    }


    @GetMapping("/usuario")
    private String usuList(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size) {
        Page<UsuarioDTO> usuarioDTOPage = usuarioService.listarTodos(PageRequest.of(page, size));
        model.addAttribute("usuarios_lista", usuarioDTOPage);
        model.addAttribute("currentPage", page);
        return "usuario/usuList";
    }


    @GetMapping("/usuario/cadastro")
    private String usuCadForm(Model model) {
        try {
            model.addAttribute("novo_usuario", new UsuarioDTO());
            model.addAttribute("func_lista", pessoaService.buscaFuncAtivoPorUsuNull());
            model.addAttribute("role_lista", Role.values());
            return "usuario/usuForm";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/usuario";
    }

    @PostMapping("usuario/cadastrar")
    private String salvarUsuario(@ModelAttribute UsuarioDTO usuario) {
        try {
            usuarioService.cadastrarUsuario(usuario);
            return "redirect:/usuario";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/usuario/cadastro";
    }

}
