package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.dto.response.HelloWorldResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @GetMapping
    public ResponseEntity<HelloWorldResponseDTO> getHelloWorld() {
     return ResponseEntity.ok(new HelloWorldResponseDTO());
    }
}
