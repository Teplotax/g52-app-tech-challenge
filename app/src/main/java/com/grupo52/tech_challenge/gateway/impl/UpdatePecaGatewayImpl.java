package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.gateway.database.model.AplicacaoProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdatePecaGatewayImpl implements UpdatePecaGateway {

    private final ProdutoRepository repository;

    @Override
    public Peca execute(Peca peca) throws GatewayException {
        try {
            Optional<ProdutoDatabase> clienteOptional = repository.findByIdAndTipoProduto(peca.getId(), TipoProduto.PECA);
            ProdutoDatabase existing = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Peça não encontrada")
            );

            ProdutoDatabase updated = ProdutoDatabase.builder()
                    .id(existing.getId())
                    .sku(peca.getSku() != null ? peca.getSku() : existing.getSku())
                    .ean(peca.getEan() != null ? peca.getEan() : existing.getEan())
                    .nome(peca.getNome() != null ? peca.getNome() : existing.getNome())
                    .preco(peca.getPreco() != null ? peca.getPreco() : existing.getPreco())
                    .estoque(peca.getEstoque() != null ? peca.getEstoque() : existing.getEstoque())
                    .estoqueReservado(existing.getEstoque())
                    .estoqueMinimo(peca.getEstoqueMinimo() != null ? peca.getEstoqueMinimo() : existing.getEstoqueMinimo())
                    .tipoPeca(peca.getTipoPeca() != null ? peca.getTipoPeca() : existing.getTipoPeca())
                    .tipoProduto(TipoProduto.PECA)
                    .aplicacoes(AplicacaoProdutoDatabase.fromDomain(peca.getAplicacoes(), existing))
                    .build();

            return repository.save(updated).toPecaDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao atualizar Peça, sku e ean devem ser únicos", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao atualizar Peça, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}