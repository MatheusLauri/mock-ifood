package com.mockifood.ifood.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonContractTest {

  private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  @Test
  void earningsResponse_deveSerializarComNomesEsperados() throws Exception {
    var entrega = new EarningsDelivery(LocalDate.of(2024, 1, 1), BigDecimal.valueOf(120.50), 8, 6);
    var resumo = new EarningsSummary(BigDecimal.valueOf(120.50), BigDecimal.valueOf(120.50), 1);
    var resp = new EarningsResponse("123", "ifood", List.of(entrega), resumo);

    var json = mapper.writeValueAsString(resp);

    assertThat(json).contains("\"userId\"");
    assertThat(json).contains("\"platform\"");
    assertThat(json).contains("\"deliveries\"");
    assertThat(json).contains("\"summary\"");
    assertThat(json).contains("\"hoursWorked\"");
    assertThat(json).contains("\"totalEarned\"");
    assertThat(json).contains("\"avgDaily\"");
    assertThat(json).contains("\"daysWorked\"");
  }

  @Test
  void performanceResponse_deveSerializarComAcceptanceRate() throws Exception {
    var resp = new PerformanceResponse("123", "ifood", 0.05, 4.7, 0.9, 0.95);
    var json = mapper.writeValueAsString(resp);

    assertThat(json).contains("\"acceptanceRate\"");
    assertThat(json).contains("\"cancellationRate\"");
    assertThat(json).contains("\"averageRating\"");
    assertThat(json).contains("\"onTimeRate\"");
  }

  @Test
  void summaryResponse_deveSerializarConsistenciaEValores() throws Exception {
    var resp = new SummaryResponse("123", "ifood", BigDecimal.valueOf(100.10), 0.77, BigDecimal.valueOf(3000.50), 20);
    var json = mapper.writeValueAsString(resp);

    assertThat(json).contains("\"consistency\"");
    assertThat(json).contains("\"avgDaily\"");
    assertThat(json).contains("\"totalEarned\"");
    assertThat(json).contains("\"daysWorked\"");
  }
}

