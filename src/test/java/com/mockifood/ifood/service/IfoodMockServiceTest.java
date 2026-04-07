package com.mockifood.ifood.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class IfoodMockServiceTest {

  private IfoodMockService novoServico(long semente) {
    var props = new IfoodMockProperties(
        semente,
        new IfoodMockProperties.Caos(false, 0, 0, 0.0, 0.0, 0)
    );
    return new IfoodMockService(props);
  }

  @Test
  void buscarGanhos_deveSerDeterministico_paraMesmosParametros() {
    var servico = novoServico(42);
    var de = LocalDate.of(2024, 1, 1);
    var ate = LocalDate.of(2024, 1, 31);

    var r1 = servico.buscarGanhos("123", de, ate, null);
    var r2 = servico.buscarGanhos("123", de, ate, null);

    assertThat(r2).isEqualTo(r1);
    assertThat(r1.plataforma()).isEqualTo("ifood");
    assertThat(r1.entregas()).allSatisfy(e -> {
      assertThat(e.data()).isBetween(de, ate);
      assertThat(e.valor().scale()).isEqualTo(2);
      assertThat(e.pedidos()).isPositive();
      assertThat(e.horasTrabalhadas()).isPositive();
    });
    assertThat(r1.resumo().diasTrabalhados()).isEqualTo(r1.entregas().size());
  }

  @Test
  void buscarGanhos_deveVariar_entreUsuariosDiferentes() {
    var servico = novoServico(42);
    var de = LocalDate.of(2024, 1, 1);
    var ate = LocalDate.of(2024, 1, 31);

    var a = servico.buscarGanhos("123", de, ate, null);
    var b = servico.buscarGanhos("999", de, ate, null);

    assertThat(a).isNotEqualTo(b);
  }

  @Test
  void buscarResumo_deveCalcularConsistencia_entre0e1() {
    var servico = novoServico(42);
    var de = LocalDate.of(2024, 1, 1);
    var ate = LocalDate.of(2024, 1, 31);

    var resumo = servico.buscarResumo("123", de, ate, null);

    assertThat(resumo.plataforma()).isEqualTo("ifood");
    assertThat(resumo.mediaDiaria()).isNotNull();
    assertThat(resumo.totalGanho()).isNotNull();
    assertThat(resumo.diasTrabalhados()).isGreaterThanOrEqualTo(0);
    assertThat(resumo.consistencia()).isBetween(0.0, 1.0);
  }

  @Test
  void buscarPerformance_deveRetornarTaxasEsperadas() {
    var servico = novoServico(42);

    var perf = servico.buscarPerformance("123", PerfilIfood.A_ESTAVEL);

    assertThat(perf.plataforma()).isEqualTo("ifood");
    assertThat(perf.taxaCancelamento()).isBetween(0.0, 1.0);
    assertThat(perf.mediaAvaliacao()).isBetween(0.0, 5.0);
    assertThat(perf.taxaAceitacao()).isBetween(0.0, 1.0);
    assertThat(perf.taxaPontualidade()).isBetween(0.0, 1.0);
  }
}

