package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.dto.response.CreatePecaResponseDTO;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/peca")
    public ResponseEntity<List<CreatePecaResponseDTO>> getPecas() {
        List<ProdutoDatabase> produtosDatabase =  produtoRepository.findAllByTipoProduto(TipoProduto.PECA);
        List<Peca> pecas = produtosDatabase.stream().map(ProdutoDatabase::toPecaDomain).toList();


     return ResponseEntity.ok(pecas.stream().map(peca -> CreatePecaResponseDTO.fromDomain(peca)).toList());
    }

    @GetMapping("/pecaById")
    public ResponseEntity<List<CreatePecaResponseDTO>> getPecasByModeloId() {
        List<ProdutoDatabase> produtosDatabase =  produtoRepository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(TipoPeca.FILTRO_OLEO, 4L, 2011, 2011);
        List<Peca> pecas = produtosDatabase.stream().map(ProdutoDatabase::toPecaDomain).toList();


        return ResponseEntity.ok(pecas.stream().map(peca -> CreatePecaResponseDTO.fromDomain(peca)).toList());
    }
}
