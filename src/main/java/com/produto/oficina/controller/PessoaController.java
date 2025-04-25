package com.produto.oficina.controller;

import com.produto.oficina.dto.pessoaCad.EnderecoDto;
import com.produto.oficina.dto.pessoaCad.PessoaDto;
import com.produto.oficina.dto.pessoaCad.TelefoneDto;
import com.produto.oficina.model.Pessoa;
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
import java.util.Objects;

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
            model.addAttribute("novo_pessoa", new PessoaDto());
            model.addAttribute("novo_endereco", new EnderecoDto());
            model.addAttribute("novo_telefone", new TelefoneDto());
            model.addAttribute("lista_tel", TipoTelefone.values());
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
        if (enderecoDto != null
                && !Objects.equals(enderecoDto.getEndRua(), "")
                && !Objects.equals(enderecoDto.getEndCep(), "")
                && !Objects.equals(enderecoDto.getEndBairro(), "")
                && enderecoDto.getCidadeId() != null) {

            enderecoDto.setCidade(cidadeService.findCidadeEestadoById(enderecoDto.getCidadeId()));

            for (EnderecoDto endereco : enderecosDtoList) {
                if (endereco.getEndNumero().equals(enderecoDto.getEndNumero()) &&
                        endereco.getEndRua().equals(enderecoDto.getEndRua()) &&
                        endereco.getEndCep().equals(enderecoDto.getEndCep()) &&
                        endereco.getEndBairro().equals(enderecoDto.getEndBairro()) &&
                        endereco.getCidade().equals(enderecoDto.getCidade())) {
                    model.addAttribute("enderecos", enderecosDtoList);
                    return "fragments/pessoaFrags/enderecoTable";
                }
            }

            if (enderecoDto.isEndPrincipal()) {
                enderecosDtoList.forEach(e -> e.setEndPrincipal(false));
            } else if (enderecosDtoList.isEmpty()) {
                enderecoDto.setEndPrincipal(true);
            }
            enderecosDtoList.add(enderecoDto);
            model.addAttribute("novo_endereco", new EnderecoDto());
        }
        model.addAttribute("enderecos", enderecosDtoList);
        return "fragments/pessoaFrags/enderecoTable";
    }

    @DeleteMapping("/cadastro/enderecos/remover/{index}")
    public String removerEnderecoLista(@PathVariable Integer index, Model model) {
        if (index >= 0 && index < enderecosDtoList.size()) {
            if (enderecosDtoList.get(index).isEndPrincipal() && enderecosDtoList.size() > 1) {
                enderecosDtoList.getFirst().setEndPrincipal(true);
            }
            enderecosDtoList.remove(index.intValue());
        }
        model.addAttribute("enderecos", enderecosDtoList);
        model.addAttribute("novo_endereco", new EnderecoDto());
        return "fragments/pessoaFrags/enderecoTable";
    }

    @GetMapping("/cadastro/enderecos/editar/{index}")
    public String editarEnderecoLista(@PathVariable Integer index, Model model) {
        model.addAttribute("novo_endereco", enderecosDtoList.get(index));
        enderecosDtoList.remove(index.intValue());
        model.addAttribute("estados", estadoService.findAll());
        return "fragments/pessoaFrags/enderecoForm";
    }

    @PostMapping("/cadastro/telefones/adicionar")
    public String adicionarTelefoneLista(@Valid @ModelAttribute TelefoneDto telefoneDto,
                                         BindingResult result,
                                         Model model) {
        if (!Objects.equals(telefoneDto.getTelNumero(), "")) {
            for (TelefoneDto telefone : telefonesDtoList) {
                if (telefone.getTelNumero().equals(telefoneDto.getTelNumero()) &&
                        telefone.getTipo().equals(telefoneDto.getTipo())) {
                    model.addAttribute("telefones", telefonesDtoList);
                    return "fragments/pessoaFrags/telefoneTable";
                }
            }
            if (telefoneDto.isTelPrincipal()) {
                telefonesDtoList.forEach(e -> e.setTelPrincipal(false));
            } else if (telefonesDtoList.isEmpty()) {
                telefoneDto.setTelPrincipal(true);
            }

            telefonesDtoList.add(telefoneDto);
        }
        model.addAttribute("telefones", telefonesDtoList);
        model.addAttribute("novo_telefone", new TelefoneDto());
        return "fragments/pessoaFrags/telefoneTable";
    }


    @DeleteMapping("/cadastro/telefones/remover/{index}")
    public String removerTelefoneLista(@PathVariable Integer index, Model model) {
        if (index >= 0 && index < telefonesDtoList.size()) {
            if (telefonesDtoList.get(index).isTelPrincipal() && telefonesDtoList.size() > 1) {
                telefonesDtoList.getFirst().setTelPrincipal(true);
            }
            telefonesDtoList.remove(index.intValue());
        }
        model.addAttribute("telefones", telefonesDtoList);
        return "fragments/pessoaFrags/telefoneTable";
    }

    @GetMapping("/cadastro/telefones/editar/{index}")
    public String editarTelefoneLista(@PathVariable Integer index, Model model) {
        model.addAttribute("novo_telefone", telefonesDtoList.get(index));
        telefonesDtoList.remove(index.intValue());
        model.addAttribute("lista_tel", TipoTelefone.values());
        return "fragments/pessoaFrags/telefoneForm";
    }


    // Métodos de editar e desativar da lista de pessoas

    @GetMapping("/editar/{index}")
    public String editarPessoa(@PathVariable Long index, Model model) {
        PessoaDto pessoa = null;
        telefonesDtoList.clear();
        enderecosDtoList.clear();
        if (index != null) {
            pessoa = pessoaService.buscaPessoaEnderecosTelefones(index);
            telefonesDtoList.addAll(pessoa.getTelefones());
            enderecosDtoList.addAll(pessoa.getEnderecos());
        }

        model.addAttribute("novo_endereco", new EnderecoDto());
        model.addAttribute("novo_telefone", new TelefoneDto());

        model.addAttribute("telefones", telefonesDtoList);
        model.addAttribute("enderecos", enderecosDtoList);
        model.addAttribute("lista_tel", TipoTelefone.values());
        model.addAttribute("estados", estadoService.findAll());
        model.addAttribute("novo_pessoa", pessoa);
        return "pessoa/pesForm";
    }

    @PostMapping("/desativar/{index}")
    public String desativarPessoa(@PathVariable Long index, Model model) {
        pessoaService.desativarPessoa(index);
        return "redirect:/pessoa";
    }

}
