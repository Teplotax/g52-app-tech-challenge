package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.EstoqueMovimentacaoRequestDTO;
import com.grupo52.tech_challenge.dto.response.EstoqueMovimentacaoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindProdutoByEanResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/produtos")
public interface EstoqueApi {

    @GetMapping("/ean/{ean}")
    ResponseEntity<FindProdutoByEanResponseDTO> findByEan(
            @PathVariable String ean) throws GatewayException;

    @PostMapping("/estoque/entrada")
    ResponseEntity<EstoqueMovimentacaoResponseDTO> entrada(
            @RequestBody @Valid List<EstoqueMovimentacaoRequestDTO> itens) throws GatewayException;

    @PostMapping("/estoque/saida")
    ResponseEntity<EstoqueMovimentacaoResponseDTO> saida(
            @RequestBody @Valid List<EstoqueMovimentacaoRequestDTO> itens) throws GatewayException;
}