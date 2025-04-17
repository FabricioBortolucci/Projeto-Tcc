package com.produto.oficina.controller;

import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.enums.TipoTelefone;
import com.produto.oficina.service.CidadeService;
import com.produto.oficina.service.EnderecoService;
import com.produto.oficina.service.EstadoService;
import com.produto.oficina.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pessoa")
public class PessoaController {


    private final PessoaService pessoaService;
    private final CidadeService cidadeService;
    private final EstadoService estadoService;
    private final EnderecoService enderecoService;

    private final List<EnderecoDto> enderecosDtoList = new ArrayList<>();
    private final List<TelefoneDto> telefonesDtoList = new ArrayList<>();

    public PessoaController(PessoaService pessoaService, CidadeService cidadeService, EstadoService estadoService, EnderecoService enderecoService) {
        this.pessoaService = pessoaService;
        this.cidadeService = cidadeService;
        this.estadoService = estadoService;
        this.enderecoService = enderecoService;
    }

    @GetMapping()
    public String pessoaList(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size) {
        enderecosDtoList.clear();
        telefonesDtoList.clear();
        Page<PessoaDto> pessoaDtoPage = pessoaService.listarTodos(PageRequest.of(page, size));
        model.addAttribute("pes_list", pessoaDtoPage);
        model.addAttribute("currentPage", page);
        return "pessoa/pesList";
    }

    @GetMapping("/cadastro")
    public String pessoaCadastro(Model model) {
        model.addAttribute("estados", estadoService.findAll());
        model.addAttribute("novo_pessoa", new PessoaDto());
        model.addAttribute("novo_endereco", new EnderecoDto());
        model.addAttribute("novo_telefone", new TelefoneDto());
        model.addAttribute("lista_tel", TipoTelefone.values());
        return "pessoa/pesForm";
    }

    @PostMapping("/cadastrar")
    public String pessoaCadastrar(@Valid @ModelAttribute PessoaDto pessoaDto,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("novo_pessoa", pessoaDto);
            return "pessoa/pesForm";
        }
        pessoaService.salvarPessoa(pessoaDto, enderecosDtoList, telefonesDtoList);

        return "redirect:/pessoa";
    }

    @GetMapping("/cadastro/cidades")
    public String atualizarCidades(@RequestParam("estado") Long estadoId, Model model) {
        model.addAttribute("cidades", cidadeService.findCidadesByEstadoId(estadoId));
        return "fragments/pessoaFrags/cidadesSelect";
    }

    @PostMapping("/cadastro/enderecos/adicionar")
    public String adicionarEnderecoLista(@Valid @ModelAttribute EnderecoDto enderecoDto,
                                         BindingResult result,
                                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("estados", estadoService.findAll());
            model.addAttribute("novo_endereco", enderecoDto);
            return "fragments/pessoaFrags/enderecoTable";
        }

        if (enderecoDto.getCidadeId() != null)
            enderecoDto.setCidade(cidadeService.findCidadeEestadoById(enderecoDto.getCidadeId()));

        if (enderecoDto.isEndPrincipal()) {
            enderecosDtoList.forEach(e -> e.setEndPrincipal(false));
        } else if (enderecosDtoList.isEmpty()) {
            enderecoDto.setEndPrincipal(true);
        }

        enderecosDtoList.add(enderecoDto);
        model.addAttribute("enderecos", enderecosDtoList);
        return "fragments/pessoaFrags/enderecoTable";
    }

    @DeleteMapping("/cadastro/enderecos/remover/{index}")
    public String removerEnderecoLista(@PathVariable Integer index, Model model) {
        if (index >= 0 && index < enderecosDtoList.size()) {
            if (enderecosDtoList.get(index).isEndPrincipal() && enderecosDtoList.size() > 1) {
                enderecosDtoList.get(index + 1).setEndPrincipal(true);
            }
            enderecosDtoList.remove(index.intValue());
        }
        model.addAttribute("enderecos", enderecosDtoList);
        return "fragments/pessoaFrags/enderecoTable";
    }

    @GetMapping("/cadastro/enderecos/editar/{index}")
    public String editarEnderecoLista(@PathVariable Integer index, Model model) {
        model.addAttribute("novo_endereco", enderecosDtoList.get(index));
        model.addAttribute("estados", estadoService.findAll());
        return "fragments/pessoaFrags/enderecoForm";
    }

    @PostMapping("/cadastro/telefones/adicionar")
    public String adicionarTelefoneLista(@Valid @ModelAttribute TelefoneDto telefoneDto,
                                         BindingResult result,
                                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("novo_telefone", telefoneDto);
            model.addAttribute("lista_tel", TipoTelefone.values());
            return "fragments/pessoaFrags/telefoneTable";
        }

        if (telefoneDto.isTelPrincipal()) {
            telefonesDtoList.forEach(e -> e.setTelPrincipal(false));
        } else if (telefonesDtoList.isEmpty()) {
            telefoneDto.setTelPrincipal(true);
        }

        telefonesDtoList.add(telefoneDto);
        model.addAttribute("telefones", telefonesDtoList);
        return "fragments/pessoaFrags/telefoneTable";
    }


    @DeleteMapping("/cadastro/telefones/remover/{index}")
    public String removerTelefoneLista(@PathVariable Integer index, Model model) {
        if (index >= 0 && index < telefonesDtoList.size()) {
            if (telefonesDtoList.get(index).isTelPrincipal() && telefonesDtoList.size() > 1) {
                telefonesDtoList.get(index + 1).setTelPrincipal(true);
            }
            telefonesDtoList.remove(index.intValue());
        }
        model.addAttribute("telefones", telefonesDtoList);
        return "fragments/pessoaFrags/telefoneTable";
    }

    @GetMapping("/cadastro/telefones/editar/{index}")
    public String editarTelefoneLista(@PathVariable Integer index, Model model) {
        model.addAttribute("novo_telefone", telefonesDtoList.get(index));
        model.addAttribute("lista_tel", TipoTelefone.values());
        return "fragments/pessoaFrags/telefoneForm";
    }
}
