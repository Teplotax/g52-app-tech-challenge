package com.grupo52.tech_challenge.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/aquisicao")
public interface AquisicaoLinkApi {

    @GetMapping("/{osId}")
    ResponseEntity<String> confirmPage(
            @PathVariable Long osId,
            @RequestParam String token);

    @PostMapping("/{osId}")
    ResponseEntity<String> confirm(
            @PathVariable Long osId,
            @RequestParam String token);
}