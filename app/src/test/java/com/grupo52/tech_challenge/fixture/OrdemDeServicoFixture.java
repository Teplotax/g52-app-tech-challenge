package com.grupo52.tech_challenge.fixture;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdemDeServicoFixture {

    public static OrdemDeServico emDiagnostico(Long osId) {
        return OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.EM_DIAGNOSTICO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(ComplexidadeOS.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("485.00"))
                .precoTotalAprovado(null)
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("0.00"))
                .precoServicosAdicionais(new BigDecimal("0.00"))
                .justificativaNecessarios(null)
                .justificativaAdicionais(null)
                .servicosDesejados(List.of(revisaoDeFreios(), balanceamentoDeRodas()))
                .servicosNecessarios(List.of())
                .servicosAdicionais(List.of())
                .historico(List.of(
                        statusChange(StatusOS.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(StatusOS.EM_DIAGNOSTICO, "2024-06-01T11:30:00")
                ))
                .build();
    }

    public static OrdemDeServico recebida(Long osId) {
        return OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.RECEBIDA)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("485.00"))
                .precoTotalAprovado(null)
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("0.00"))
                .precoServicosAdicionais(new BigDecimal("0.00"))
                .justificativaNecessarios(null)
                .justificativaAdicionais(null)
                .servicosDesejados(List.of(revisaoDeFreiosComDiscos(), balanceamentoDeRodasComMassa()))
                .servicosNecessarios(List.of())
                .servicosAdicionais(List.of())
                .build();
    }

    public static Cliente clienteJoaoSilva() {
        return Cliente.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .documento("123.456.789-00")
                .build();
    }

    public static Veiculo veiculoCorollaABC1D23() {
        return Veiculo.builder()
                .id(1L)
                .placa("ABC1D23")
                .build();
    }

    public static ServicoOS revisaoDeFreios() {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(List.of(
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .nome("Pastilha de freio dianteira")
                                        .ean("7891234560001")
                                        .preco(new BigDecimal("35.00"))
                                        .build())
                                .quantidade(4)
                                .precoTotal(new BigDecimal("140.00"))
                                .build()
                ))
                .insumos(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Fluido de freio DOT 4")
                                        .ean("7891234560010")
                                        .preco(new BigDecimal("25.00"))
                                        .build())
                                .quantidade(1)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                ))
                .build();
    }

    public static ServicoOS balanceamentoDeRodas() {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(List.of())
                .insumos(List.of())
                .build();
    }

    public static ServicoOS revisaoDeFreiosComDiscos() {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(List.of(
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .nome("Pastilha de freio dianteira")
                                        .ean("7891234560001")
                                        .preco(new BigDecimal("35.00"))
                                        .build())
                                .quantidade(4)
                                .precoTotal(new BigDecimal("140.00"))
                                .build(),
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .nome("Disco de freio dianteiro")
                                        .ean("7891234560002")
                                        .preco(new BigDecimal("30.00"))
                                        .build())
                                .quantidade(2)
                                .precoTotal(new BigDecimal("60.00"))
                                .build()
                ))
                .insumos(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Fluido de freio DOT 4")
                                        .ean("7891234560010")
                                        .preco(new BigDecimal("25.00"))
                                        .build())
                                .quantidade(1)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                ))
                .build();
    }

    public static ServicoOS balanceamentoDeRodasComMassa() {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(List.of())
                .insumos(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Massa de balanceamento")
                                        .ean("7891234560011")
                                        .preco(new BigDecimal("12.50"))
                                        .build())
                                .quantidade(2)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                ))
                .build();
    }

    private static StatusChange statusChange(StatusOS status, String createdAt) {
        return StatusChange.builder()
                .status(status)
                .createdAt(LocalDateTime.parse(createdAt))
                .build();
    }
}