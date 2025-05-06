package com.produto.oficina.service;

import com.produto.oficina.Utils.JavaUtils;
import com.produto.oficina.model.*;
import com.produto.oficina.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

@Service
public class CaixaService {


    private final CaixaRepository caixaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final MovimentacaoCaixaRepository movimentacaoCaixaRepository;

    public CaixaService(CaixaRepository caixaRepository, UsuarioRepository usuarioRepository, PessoaRepository pessoaRepository, MovimentacaoCaixaRepository movimentacaoCaixaRepository) {
        this.caixaRepository = caixaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pessoaRepository = pessoaRepository;
        this.movimentacaoCaixaRepository = movimentacaoCaixaRepository;
    }

    public Page<Caixa> findAll(Pageable pageable) {
        return caixaRepository.findAll(pageable);
    }

    public Optional<Caixa> findById(Long index) {
        return caixaRepository.findById(index);
    }

    public void save(Caixa caixa) {
        if (caixa != null) {
            caixa.setCriouCaixa(buscaPessoaCriouCaixa());
            caixa.setFechado(false);
            caixaRepository.saveAndFlush(caixa);
        }
    }

    private Pessoa buscaPessoaCriouCaixa() {
        String username = JavaUtils.getUsuarioLogadoUsername();
        if (username != null) {
            Usuario usuario = usuarioRepository.findUsuarioByUsuNome(username);
            Pessoa pes = pessoaRepository.findPessoaByUsuario(usuario);
            if (usuario != null && usuario.getPessoaRel() != null) {
                return pes;
            }
        }
        return null;
    }

    public Caixa buscarCaixaView(Long index) {
        Caixa caixa = caixaRepository.findById(index).get();
        caixa.setCriouCaixa(pessoaRepository.findById(caixa.getCriouCaixa().getId()).get());
        return caixa;
    }

    public MovimentacaoCaixa buscaMovimentacaoCaixa(Long index) {
        return movimentacaoCaixaRepository.findAllByCaixa_Id(index);
    }

    public boolean verificaCaixaAberto() {
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.now().atTime(LocalTime.MAX);

        Caixa caixaHoje = caixaRepository.findCaixaByFechadoAndDataCadastroBetween(false, inicioDoDia, fimDoDia);
        return caixaHoje == null;
    }

    public Caixa novoCaixa() {
        Caixa caixa = new Caixa();

        Caixa caixaDiaAnterior = caixaRepository.findFirstByFechadoTrueOrderByDataCadastroDesc();
        if (caixaDiaAnterior != null) caixa.setValor(caixaDiaAnterior.getValor());
        return caixa;
    }

    public void fecharCaixa(Long index) {
        caixaRepository.findById(index).ifPresent(caixa -> {
            caixa.setDataFechamento(LocalDateTime.now());
            caixa.setFechado(true);
            caixaRepository.saveAndFlush(caixa);
        });
    }
}
