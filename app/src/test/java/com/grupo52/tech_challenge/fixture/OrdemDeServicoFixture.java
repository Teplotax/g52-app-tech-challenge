package com.grupo52.tech_challenge.fixture;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreios(false), balanceamentoDeRodas(false))))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(StatusOS.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(StatusOS.EM_DIAGNOSTICO, "2024-06-01T11:30:00")
                )))
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
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreiosComDiscos(false), balanceamentoDeRodasComMassa(false))))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();
    }

    public static OrdemDeServico aguardandoAprovacao(Long osId) {
        return OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.AGUARDANDO_APROVACAO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(ComplexidadeOS.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoTotalAprovado(null)
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .justificativaNecessarios("Identificado desgaste excessivo no disco de freio durante o diagnóstico")
                .justificativaAdicionais("Mecânico identificou lâmpada do farol direito queimada durante o diagnóstico")
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreios(false), balanceamentoDeRodas(false))))
                .servicosNecessarios(new ArrayList<>(List.of(trocaDeDisco(false))))
                .servicosAdicionais(new ArrayList<>(List.of(trocaDeLampada(false))))
                .historico(new ArrayList<>(List.of(
                        statusChange(StatusOS.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(StatusOS.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(StatusOS.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00")
                )))
                .build();
    }

    public static OrdemDeServico aguardandoAprovacaoComIds(Long osId) {
        return OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.AGUARDANDO_APROVACAO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(ComplexidadeOS.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoTotalAprovado(null)
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .justificativaNecessarios("Identificado desgaste excessivo no disco de freio durante o diagnóstico")
                .justificativaAdicionais("Mecânico identificou lâmpada do farol direito queimada durante o diagnóstico")
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreiosComId(1L, false), balanceamentoDeRodasComId(2L, false))))
                .servicosNecessarios(new ArrayList<>(List.of(trocaDeDiscoComId(3L, false))))
                .servicosAdicionais(new ArrayList<>(List.of(trocaDeLampadaComId(4L, false))))
                .historico(new ArrayList<>(List.of(
                        statusChange(StatusOS.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(StatusOS.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(StatusOS.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00")
                )))
                .build();
    }

    public static Cliente clienteJoaoSilva() {
        return Cliente.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .documento("123.456.789-00")
                .email("joao.silva@email.com")
                .build();
    }

    public static Veiculo veiculoCorollaABC1D23() {
        return Veiculo.builder()
                .id(1L)
                .placa("ABC1D23")
                .build();
    }

    public static ServicoOS revisaoDeFreios(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .nome("Pastilha de freio dianteira")
                                        .ean("7891234560001")
                                        .preco(new BigDecimal("35.00"))
                                        .build())
                                .quantidade(4)
                                .precoTotal(new BigDecimal("140.00"))
                                .build()
                )))
                .insumos(new ArrayList<>(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Fluido de freio DOT 4")
                                        .ean("7891234560010")
                                        .preco(new BigDecimal("25.00"))
                                        .build())
                                .quantidade(1)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                )))
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS revisaoDeFreiosComId(Long id, Boolean aprovado) {
        return ServicoOS.builder()
                .id(id)
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS balanceamentoDeRodas(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS balanceamentoDeRodasComId(Long id, Boolean aprovado) {
        return ServicoOS.builder()
                .id(id)
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS trocaDeDisco(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Troca de disco de freio").build())
                .precoTotal(new BigDecimal("280.00"))
                .precoHorasTecnicas(new BigDecimal("100.00"))
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .id(21L)
                                        .nome("Disco de freio dianteiro")
                                        .ean("7891234560020")
                                        .preco(new BigDecimal("90.00"))
                                        .build())
                                .quantidade(2)
                                .precoTotal(new BigDecimal("180.00"))
                                .build()
                )))
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS trocaDeDiscoComId(Long id, Boolean aprovado) {
        return ServicoOS.builder()
                .id(id)
                .servico(Servico.builder().nome("Troca de disco de freio").build())
                .precoTotal(new BigDecimal("280.00"))
                .precoHorasTecnicas(new BigDecimal("100.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS trocaDeLampada(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Troca de lâmpada do farol").build())
                .precoTotal(new BigDecimal("45.00"))
                .precoHorasTecnicas(new BigDecimal("21.00"))
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder()
                                .peca(Peca.builder()
                                        .id(23L)
                                        .nome("Lâmpada de Farol H7 Luz Baixa")
                                        .ean("7891342130177")
                                        .preco(new BigDecimal("24.00"))
                                        .build())
                                .quantidade(1)
                                .precoTotal(new BigDecimal("24.00"))
                                .build()
                )))
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS trocaDeLampadaComId(Long id, Boolean aprovado) {
        return ServicoOS.builder()
                .id(id)
                .servico(Servico.builder().nome("Troca de lâmpada do farol").build())
                .precoTotal(new BigDecimal("45.00"))
                .precoHorasTecnicas(new BigDecimal("21.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS revisaoDeFreiosComDiscos(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>(List.of(
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
                )))
                .insumos(new ArrayList<>(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Fluido de freio DOT 4")
                                        .ean("7891234560010")
                                        .preco(new BigDecimal("25.00"))
                                        .build())
                                .quantidade(1)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                )))
                .aprovado(aprovado)
                .build();
    }

    public static ServicoOS balanceamentoDeRodasComMassa(Boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>(List.of(
                        InsumoOS.builder()
                                .insumo(Insumo.builder()
                                        .nome("Massa de balanceamento")
                                        .ean("7891234560011")
                                        .preco(new BigDecimal("12.50"))
                                        .build())
                                .quantidade(2)
                                .precoTotal(new BigDecimal("25.00"))
                                .build()
                )))
                .aprovado(aprovado)
                .build();
    }

    private static StatusChange statusChange(StatusOS status, String createdAt) {
        return StatusChange.builder()
                .status(status)
                .createdAt(LocalDateTime.parse(createdAt))
                .build();
    }
}