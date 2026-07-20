package com.grupo52.tech_challenge.fixture;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemDeServicoFixture {

    public static Ordem emDiagnostico(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.EM_DIAGNOSTICO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("485.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("0.00"))
                .precoServicosAdicionais(new BigDecimal("0.00"))
                .justificativaNecessarios(null)
                .justificativaAdicionais(null)
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreios(false), balanceamentoDeRodas(false))))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00")
                )))
                .build();
    }

    public static Ordem recebida(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.RECEBIDA)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("485.00"))
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

    public static Ordem aguardandoAprovacao(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.AGUARDANDO_APROVACAO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .justificativaNecessarios("Identificado desgaste excessivo no disco de freio durante o diagnóstico")
                .justificativaAdicionais("Mecânico identificou lâmpada do farol direito queimada durante o diagnóstico")
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreios(false), balanceamentoDeRodas(false))))
                .servicosNecessarios(new ArrayList<>(List.of(trocaDeDisco(false))))
                .servicosAdicionais(new ArrayList<>(List.of(trocaDeLampada(false))))
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00")
                )))
                .build();
    }

    public static Ordem finalizada(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.FINALIZADA)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00"),
                        statusChange(Status.APROVADA, "2024-06-01T15:30:00"),
                        statusChange(Status.EM_EXECUCAO, "2024-06-01T16:00:00"),
                        statusChange(Status.FINALIZADA, "2024-06-02T09:00:00")
                )))
                .build();
    }

    public static Ordem entregue(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.ENTREGUE)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave(null)
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00"),
                        statusChange(Status.APROVADA, "2024-06-01T15:30:00"),
                        statusChange(Status.EM_EXECUCAO, "2024-06-01T16:00:00"),
                        statusChange(Status.FINALIZADA, "2024-06-02T09:00:00"),
                        statusChange(Status.ENTREGUE, "2024-06-02T10:00:00")
                )))
                .build();
    }

    public static Ordem devolvido(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.DEVOLVIDO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave(null)
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00"),
                        statusChange(Status.CANCELADA, "2024-06-01T16:00:00"),
                        statusChange(Status.DEVOLVIDO, "2024-06-02T10:00:00")
                )))
                .build();
    }

    public static Ordem cancelada(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.CANCELADA)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00"),
                        statusChange(Status.CANCELADA, "2024-06-01T16:00:00")
                )))
                .build();
    }

    public static Ordem aguardandoAprovacaoComIds(Long osId) {
        return Ordem.builder()
                .id(osId)
                .status(Status.AGUARDANDO_APROVACAO)
                .cliente(clienteJoaoSilva())
                .veiculo(veiculoCorollaABC1D23())
                .complexidade(Complexidade.MEDIA)
                .sintomas("Barulho ao frear e vibração no volante em altas velocidades")
                .tagChave("001")
                .criadaEm(LocalDateTime.parse("2024-06-01T10:00:00"))
                .precoTotal(new BigDecimal("810.00"))
                .precoServicosDesejados(new BigDecimal("485.00"))
                .precoServicosNecessarios(new BigDecimal("280.00"))
                .precoServicosAdicionais(new BigDecimal("45.00"))
                .justificativaNecessarios("Identificado desgaste excessivo no disco de freio durante o diagnóstico")
                .justificativaAdicionais("Mecânico identificou lâmpada do farol direito queimada durante o diagnóstico")
                .servicosDesejados(new ArrayList<>(List.of(revisaoDeFreiosComId(1L, false), balanceamentoDeRodasComId(2L, false))))
                .servicosNecessarios(new ArrayList<>(List.of(trocaDeDiscoComId(3L, false))))
                .servicosAdicionais(new ArrayList<>(List.of(trocaDeLampadaComId(4L, false))))
                .historico(new ArrayList<>(List.of(
                        statusChange(Status.RECEBIDA, "2024-06-01T10:00:00"),
                        statusChange(Status.EM_DIAGNOSTICO, "2024-06-01T11:30:00"),
                        statusChange(Status.AGUARDANDO_APROVACAO, "2024-06-01T14:00:00")
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

    public static OrdemServico revisaoDeFreios(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder()
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
                        OrdemInsumo.builder()
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

    public static OrdemServico revisaoDeFreiosComId(Long id, Boolean aprovado) {
        return OrdemServico.builder()
                .id(id)
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static OrdemServico balanceamentoDeRodas(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static OrdemServico balanceamentoDeRodasComId(Long id, Boolean aprovado) {
        return OrdemServico.builder()
                .id(id)
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static OrdemServico trocaDeDisco(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de disco de freio").build())
                .precoTotal(new BigDecimal("280.00"))
                .precoHorasTecnicas(new BigDecimal("100.00"))
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder()
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

    public static OrdemServico trocaDeDiscoComId(Long id, Boolean aprovado) {
        return OrdemServico.builder()
                .id(id)
                .servico(Servico.builder().nome("Troca de disco de freio").build())
                .precoTotal(new BigDecimal("280.00"))
                .precoHorasTecnicas(new BigDecimal("100.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static OrdemServico trocaDeLampada(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de lâmpada do farol").build())
                .precoTotal(new BigDecimal("45.00"))
                .precoHorasTecnicas(new BigDecimal("21.00"))
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder()
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

    public static OrdemServico trocaDeLampadaComId(Long id, Boolean aprovado) {
        return OrdemServico.builder()
                .id(id)
                .servico(Servico.builder().nome("Troca de lâmpada do farol").build())
                .precoTotal(new BigDecimal("45.00"))
                .precoHorasTecnicas(new BigDecimal("21.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(aprovado)
                .build();
    }

    public static OrdemServico revisaoDeFreiosComDiscos(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .precoTotal(new BigDecimal("320.00"))
                .precoHorasTecnicas(new BigDecimal("120.00"))
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder()
                                .peca(Peca.builder()
                                        .nome("Pastilha de freio dianteira")
                                        .ean("7891234560001")
                                        .preco(new BigDecimal("35.00"))
                                        .build())
                                .quantidade(4)
                                .precoTotal(new BigDecimal("140.00"))
                                .build(),
                        OrdemPeca.builder()
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
                        OrdemInsumo.builder()
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

    public static OrdemServico balanceamentoDeRodasComMassa(Boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").build())
                .precoTotal(new BigDecimal("165.00"))
                .precoHorasTecnicas(new BigDecimal("80.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>(List.of(
                        OrdemInsumo.builder()
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

    private static StatusChange statusChange(Status status, String createdAt) {
        return StatusChange.builder()
                .status(status)
                .createdAt(LocalDateTime.parse(createdAt))
                .build();
    }
}