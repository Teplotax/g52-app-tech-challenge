package com.grupo52.tech_challenge.dto;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.request.CreateOrderRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateOrderRequestDTOTest {

    // sem o RECEBIDA no histórico a OS nova não gera OrdemStatusAlterado
    @Test
    void osNovaNasceRecebidaComHistorico() {
        CreateOrderRequestDTO dto = new CreateOrderRequestDTO();
        dto.setPlaca("ABC1A23");
        dto.setTagChave("001");
        dto.setServicosDesejados(List.of(1L));

        Ordem os = dto.toDomain();

        assertEquals(Status.RECEBIDA, os.getStatus());
        assertEquals(1, os.getHistorico().size());
        assertEquals(Status.RECEBIDA, os.getHistorico().get(0).getStatus());
    }
}
