package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoDatabase, Long> {

    List<ProdutoDatabase> findAllByTipoProduto(TipoProduto tipoProduto);

    List<ProdutoDatabase> findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
            TipoPeca tipoPeca, Long modeloId, Integer anoInicio, Integer anoFim
    );
}
