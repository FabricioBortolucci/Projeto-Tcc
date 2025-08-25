package com.produto.oficina.service;

import com.produto.oficina.model.MovimentacaoEstoque;
import com.produto.oficina.model.Produto;
import com.produto.oficina.model.enums.ProdutoTipo;
import com.produto.oficina.model.enums.TipoMovimentacao;
import com.produto.oficina.repository.MovimentacaoEstoqueRepository;
import com.produto.oficina.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {


    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PessoaService pessoaService;


    public Page<Produto> findAll(Pageable pageable) {
        return produtoRepository.findAll(pageable);
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public List<Produto> buscaProdutosAtivos() {
        return produtoRepository.findAllByAtivo(true);
    }

    public void saveEdit(Produto produto) {
        produtoRepository.save(produto);
    }
    public void save(Produto produto) {
        Produto prodDB = produtoRepository.findById(produto.getId()).get();
        if (!produto.getEstoque().equals(prodDB.getEstoque())) {
            MovimentacaoEstoque movEstoque = new MovimentacaoEstoque();
            movEstoque.setProduto(produto);
            movEstoque.setCustoUnitario(produto.getPrecoUnitario());
            movEstoque.setUsuarioResponsavel(pessoaService.buscaPessoaLogada());
            movEstoque.setQuantidade(BigDecimal.valueOf(produto.getEstoque()));
            if (produto.getEstoque() > prodDB.getEstoque()) {
                movEstoque.setTipo(TipoMovimentacao.ENTRADA);
                movEstoque.setObservacao("Entrada de material. ID [" + produto.getId() + "]");
            } else if (produto.getEstoque() < prodDB.getEstoque()) {
                movEstoque.setTipo(TipoMovimentacao.SAIDA);
                movEstoque.setObservacao("Saída de material. ID [" + produto.getId() + "]");
            }
            movEstoque.setDataMovimentacao(LocalDateTime.now());
            movEstoque.setOrigemId(produto.getId());
            movEstoque.setOrigemTipo("CADASTRO_" + produto.getProdutoTipo().descricao.toUpperCase());
            movimentacaoEstoqueRepository.save(movEstoque);
        }
        produtoRepository.saveAndFlush(produto);
    }

    public void deleteProd(Long produto) {
        produtoRepository.findById(produto).ifPresent(produto1 -> {
            produto1.setAtivo(false);
            produtoRepository.saveAndFlush(produto1);
        });
    }

    public Produto findById(Long index) {
        if (produtoRepository.findById(index).isPresent()) {
            return produtoRepository.findById(index).get();
        }
        return null;
    }

    public Produto findByIdView(Long index) {
        return produtoRepository.findById(index).get();
    }

    public List<Produto> buscarPorNome(String query) {
        return produtoRepository.findByNomeContainingIgnoreCase(query);
    }

    public void salvarTodos(List<Produto> produtos) {
        produtoRepository.saveAll(produtos);
    }
}
