package com.grupo52.tech_challenge.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/aprovacao")
public interface ApprovalLinkApi {

    @GetMapping("/{osId}")
    ResponseEntity<String> confirmPage(
            @PathVariable Long osId,
            @RequestParam String token);

    @PostMapping("/{osId}")
    ResponseEntity<String> approve(
            @PathVariable Long osId,
            @RequestParam String token,
            @RequestParam(name = "servicosAprovados", required = false) List<Long> servicosAprovados);
}