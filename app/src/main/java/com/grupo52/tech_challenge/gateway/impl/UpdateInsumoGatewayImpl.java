package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.database.model.AplicacaoProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateInsumoGatewayImpl implements UpdateInsumoGateway {

    private final ProdutoRepository repository;

    @Override
    public Insumo execute(Insumo insumo) throws GatewayException {
        try {
            Optional<ProdutoDatabase> clienteOptional = repository.findByIdAndTipoProduto(insumo.getId(), TipoProduto.INSUMO);
            ProdutoDatabase existing = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Insumo não encontrada")
            );

            ProdutoDatabase updated = ProdutoDatabase.builder()
                    .id(existing.getId())
                    .sku(insumo.getSku() != null ? insumo.getSku() : existing.getSku())
                    .ean(insumo.getEan() != null ? insumo.getEan() : existing.getEan())
                    .nome(insumo.getNome() != null ? insumo.getNome() : existing.getNome())
                    .preco(insumo.getPreco() != null ? insumo.getPreco() : existing.getPreco())
                    .estoque(insumo.getEstoque() != null ? insumo.getEstoque() : existing.getEstoque())
                    .estoqueReservado(existing.getEstoqueReservado())
                    .estoqueMinimo(insumo.getEstoqueMinimo() != null ? insumo.getEstoqueMinimo() : existing.getEstoqueMinimo())
                    .tipoInsumo(insumo.getTipoInsumo() != null ? insumo.getTipoInsumo() : existing.getTipoInsumo())
                    .tipoProduto(TipoProduto.INSUMO)
                    .quantidadeEmbalagem(insumo.getQuantidadeEmbalagem() != null ? insumo.getQuantidadeEmbalagem() : existing.getQuantidadeEmbalagem())
                    .unidadeDeMedida(insumo.getUnidadeDeMedida() != null ? insumo.getUnidadeDeMedida() : existing.getUnidadeDeMedida())
                    .aplicacoes(AplicacaoProdutoDatabase.fromDomain(insumo.getAplicacoes(), existing))
                    .build();

            return repository.save(updated).toInsumoDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao atualizar Insumo, sku e ean devem ser únicos", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao atualizar Insumo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}