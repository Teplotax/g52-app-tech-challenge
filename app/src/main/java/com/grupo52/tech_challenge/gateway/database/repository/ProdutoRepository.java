package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoDatabase, Long> {

    Optional<ProdutoDatabase> findByIdAndTipoProduto(Long id, TipoProduto tipoProduto);

    Boolean existsByIdAndTipoProduto(Long id, TipoProduto tipoProduto);

    List<ProdutoDatabase> findAllByTipoProduto(TipoProduto tipoProduto);

    List<ProdutoDatabase> findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
            TipoPeca tipoPeca, Long modeloId, Integer anoInicio, Integer anoFim
    );

    Optional<ProdutoDatabase> findByEan(String ean);

    List<ProdutoDatabase> findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
            TipoInsumo tipoInsumo, Long modeloId, Integer anoInicio, Integer anoFim
    );
}