package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.dto.request.EstoqueMovimentacaoRequestDTO;
import com.grupo52.tech_challenge.dto.response.EstoqueMovimentacaoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.MovimentarEstoqueGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos/estoque")
@Validated
public class EstoqueController {

    @Autowired
    private MovimentarEstoqueGateway movimentarEstoqueGateway;

    @PostMapping("/entrada")
    public ResponseEntity<EstoqueMovimentacaoResponseDTO> entrada(
            @RequestBody @Valid List<EstoqueMovimentacaoRequestDTO> itens) throws GatewayException {

        List<String> eans = itens.stream().map(EstoqueMovimentacaoRequestDTO::getEan).toList();
        List<Integer> quantidades = itens.stream().map(EstoqueMovimentacaoRequestDTO::getQuantidade).toList();

        List<Produto> produtos = movimentarEstoqueGateway.entrada(eans, quantidades);

        return ResponseEntity.ok(EstoqueMovimentacaoResponseDTO.fromDomain(produtos));
    }

    @PostMapping("/saida")
    public ResponseEntity<EstoqueMovimentacaoResponseDTO> saida(
            @RequestBody @Valid List<EstoqueMovimentacaoRequestDTO> itens) throws GatewayException {

        List<String> eans = itens.stream().map(EstoqueMovimentacaoRequestDTO::getEan).toList();
        List<Integer> quantidades = itens.stream().map(EstoqueMovimentacaoRequestDTO::getQuantidade).toList();

        List<Produto> produtos = movimentarEstoqueGateway.saida(eans, quantidades);

        return ResponseEntity.ok(EstoqueMovimentacaoResponseDTO.fromDomain(produtos));
    }
}