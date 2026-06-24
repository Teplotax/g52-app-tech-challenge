package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.MovimentarEstoqueGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentarEstoqueGatewayImpl implements MovimentarEstoqueGateway {

    private final ProdutoRepository repository;

    @Override
    @Transactional
    public List<Produto> entrada(List<String> eans, List<Integer> quantidades) throws GatewayException {
        try {
            List<Produto> resultado = new ArrayList<>();
            for (int i = 0; i < eans.size(); i++) {
                ProdutoDatabase existing = findByEan(eans.get(i));
                ProdutoDatabase updated = withEstoque(existing, existing.getEstoque() + quantidades.get(i));
                resultado.add(toDomain(repository.save(updated)));
            }
            return resultado;
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao registrar entrada de estoque: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Produto> saida(List<String> eans, List<Integer> quantidades) throws GatewayException {
        try {
            List<Produto> resultado = new ArrayList<>();
            for (int i = 0; i < eans.size(); i++) {
                ProdutoDatabase existing = findByEan(eans.get(i));
                int novoEstoque = existing.getEstoque() - quantidades.get(i);
                if (novoEstoque < 0) {
                    throw new GatewayException(
                            "Estoque insuficiente para EAN '" + eans.get(i) + "'. Disponível: " + existing.getEstoque(), 422
                    );
                }
                ProdutoDatabase updated = withEstoque(existing, novoEstoque);
                resultado.add(toDomain(repository.save(updated)));
            }
            return resultado;
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao registrar saída de estoque: " + e.getMessage(), e);
        }
    }

    private ProdutoDatabase findByEan(String ean) throws GatewayException {
        return repository.findByEan(ean)
                .orElseThrow(() -> new NotFoundGatewayException("Produto não encontrado para EAN: " + ean));
    }

    private ProdutoDatabase withEstoque(ProdutoDatabase existing, int novoEstoque) {
        ProdutoDatabase updated = ProdutoDatabase.builder()
                .id(existing.getId())
                .sku(existing.getSku())
                .ean(existing.getEan())
                .nome(existing.getNome())
                .preco(existing.getPreco())
                .estoque(novoEstoque)
                .estoqueReservado(existing.getEstoqueReservado())
                .estoqueMinimo(existing.getEstoqueMinimo())
                .tipoProduto(existing.getTipoProduto())
                .tipoPeca(existing.getTipoPeca())
                .tipoInsumo(existing.getTipoInsumo())
                .quantidadeEmbalagem(existing.getQuantidadeEmbalagem())
                .unidadeDeMedida(existing.getUnidadeDeMedida())
                .aplicacoes(existing.getAplicacoes())
                .build();
        return updated;
    }

    private Produto toDomain(ProdutoDatabase db) {
        return switch (db.getTipoProduto()) {
            case PECA -> db.toPecaDomain();
            case INSUMO -> db.toInsumoDomain();
        };
    }
}