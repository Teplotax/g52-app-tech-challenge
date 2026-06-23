package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;

import java.math.BigDecimal;
import java.util.List;

public interface Produto {

    Long getId();
    String getSku();
    String getEan();
    String getNome();
    TipoProduto getTipoProduto();
    Integer getEstoque();
    Integer getEstoqueReservado();
    Integer getEstoqueMinimo();
    BigDecimal getPreco();
    List<AplicacaoProduto> getAplicacoes();

    void adicionarEstoqueReservado(Integer quantidade);
    void removerEstoqueReservado(Integer quantidade);
    void removerEstoque(Integer quantidade);
}