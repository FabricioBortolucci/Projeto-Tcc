package com.produto.oficina.service;

import com.produto.oficina.model.PlanoDeContas;
import com.produto.oficina.model.enums.NaturezaContaPlanoContas;
import com.produto.oficina.model.enums.TipoContaPlanoContas;
import com.produto.oficina.repository.PlanoDeContasRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PlanoDeContasService {
    private final PlanoDeContasRepository planoDeContasRepository;

    public PlanoDeContasService(PlanoDeContasRepository planoDeContasRepository) {
        this.planoDeContasRepository = planoDeContasRepository;
    }

    public Page<PlanoDeContas> findPaginated(String filtro, Pageable pageable) {
        // Se o filtro NÃO for nulo e NÃO estiver em branco...
        if (filtro != null && !filtro.trim().isEmpty()) {
            // ...chama o novo método de busca, passando o filtro para ambos os campos.
            return planoDeContasRepository.findByCodigoContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
                    filtro, filtro, pageable);
        } else {
            // ...senão, chama a busca padrão que retorna todos.
            return planoDeContasRepository.findAll(pageable);
        }
    }

    @Transactional
    public PlanoDeContas salvar(PlanoDeContas planoDeContas) {
        // 1. Validação de Unicidade do Código
        validarCodigoUnico(planoDeContas);

        // 2. Validação da Conta Pai
        if (planoDeContas.getContaPai() != null) {
            // Recarrega a conta pai do banco para garantir que os dados estão corretos
            PlanoDeContas contaPai = planoDeContasRepository.findById(planoDeContas.getContaPai().getId())
                    .orElseThrow(() -> new RuntimeException("Conta Pai selecionada não existe."));

            if (contaPai.getTipoConta() != TipoContaPlanoContas.SINTETICA) {
                throw new RuntimeException("A Conta Pai selecionada deve ser do tipo Sintética (agrupadora).");
            }

            // 3. Regra de Negócio: Herdar a Natureza da Conta Pai
            // Garante consistência na hierarquia (ex: uma despesa não pode estar debaixo de uma receita)
            planoDeContas.setNaturezaConta(contaPai.getNaturezaConta());
            planoDeContas.setContaPai(contaPai); // Garante que o objeto está totalmente gerenciado
        }

        // 4. Regra de Negócio: Definir 'aceitaLancamentos' com base no 'tipoConta'
        // ESSA É A GARANTIA LÓGICA do que o JavaScript faz visualmente.
        planoDeContas.setAceitaLancamentos(planoDeContas.getTipoConta() == TipoContaPlanoContas.ANALITICA);

        // Validações adicionais para edição
        if (planoDeContas.getId() != null) {
            PlanoDeContas contaExistente = planoDeContasRepository.findById(planoDeContas.getId())
                    .orElseThrow(() -> new RuntimeException("Conta que está sendo editada não foi encontrada."));

            // Regra: Não permitir mudar o tipo de Sintética para Analítica se a conta já tiver filhas
            if (contaExistente.getTipoConta() == TipoContaPlanoContas.SINTETICA &&
                    planoDeContas.getTipoConta() == TipoContaPlanoContas.ANALITICA &&
                    !contaExistente.getContasFilhas().isEmpty()) {
                throw new RuntimeException("Não é possível alterar o tipo para 'Analítica' pois esta conta já possui contas filhas.");
            }
        }

        return planoDeContasRepository.save(planoDeContas);
    }

    private void validarCodigoUnico(PlanoDeContas planoDeContas) {
        if (planoDeContas.getId() == null) { // Criando uma nova conta
            if (planoDeContasRepository.existsByCodigo(planoDeContas.getCodigo())) {
                throw new RuntimeException("O código '" + planoDeContas.getCodigo() + "' já está em uso.");
            }
        } else { // Editando uma conta existente
            // Verifica se o novo código já existe em OUTRA conta
            planoDeContasRepository.findByCodigoAndIdNot(planoDeContas.getCodigo(), planoDeContas.getId())
                    .ifPresent(contaExistente -> {
                        throw new RuntimeException("O código '" + planoDeContas.getCodigo() + "' já está em uso por outra conta.");
                    });
        }
    }

    // Método para popular o <select> de contas pai no formulário
    public List<PlanoDeContas> buscarContasPaiDisponiveis() {
        return planoDeContasRepository.findAllByTipoContaOrderByCodigoAsc(TipoContaPlanoContas.SINTETICA);
    }

    public Optional<PlanoDeContas> findById(Long id) {
        return planoDeContasRepository.findById(id);
    }

    public List<PlanoDeContas> buscarContasParaSaidaDeCaixa() {
        List<NaturezaContaPlanoContas> naturezasDeSaida = Arrays.asList(
                NaturezaContaPlanoContas.DESPESA,
                NaturezaContaPlanoContas.CUSTO,
                NaturezaContaPlanoContas.PASSIVO
        );
        return planoDeContasRepository.findAllByTipoContaAndNaturezaContaInOrderByCodigoAsc(
                TipoContaPlanoContas.ANALITICA,
                naturezasDeSaida
        );
    }

    public List<PlanoDeContas> buscarContasParaEntradaDeCaixa() {
        List<NaturezaContaPlanoContas> naturezasDeEntrada = Arrays.asList(
                NaturezaContaPlanoContas.RECEITA,
                NaturezaContaPlanoContas.PATRIMONIO_LIQUIDO,
                NaturezaContaPlanoContas.PASSIVO // Para casos de empréstimos
        );
        return planoDeContasRepository.findAllByTipoContaAndNaturezaContaInOrderByCodigoAsc(
                TipoContaPlanoContas.ANALITICA,
                naturezasDeEntrada
        );
    }

    public void excluirPlano(Long id) {
        planoDeContasRepository.deleteById(id);
    }

    public List<PlanoDeContas> buscarContasParaProdServ() {
        List<NaturezaContaPlanoContas> naturezasDeEntrada = List.of(
                NaturezaContaPlanoContas.RECEITA
        );
        return planoDeContasRepository.findAllByTipoContaAndNaturezaContaInOrderByCodigoAsc(
                TipoContaPlanoContas.ANALITICA,
                naturezasDeEntrada
        );
    }



    public List<PlanoDeContas> buscarContasPorNatureza(NaturezaContaPlanoContas naturezaContaPlanoContas) {
        return planoDeContasRepository.findAllByNaturezaContaOrderByCodigoAsc(naturezaContaPlanoContas);
    }

    public List<PlanoDeContas> buscarContasIn(TipoContaPlanoContas tipoContaPlanoContas, List<NaturezaContaPlanoContas>  naturezaContas) {
        return planoDeContasRepository.findAllByTipoContaAndNaturezaContaInOrderByCodigoAsc(tipoContaPlanoContas, naturezaContas);
    }

    public List<PlanoDeContas> buscarContasPorCodigo(String codigo) {
        return planoDeContasRepository.findAllByCodigoStartsWithOrderByCodigoAsc(codigo);
    }
    public Optional<PlanoDeContas> buscarContaPorCodigo(String codigo) {
        return planoDeContasRepository.findByCodigo(codigo);
    }
}
