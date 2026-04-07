package com.mockifood.ifood.service;

import com.mockifood.ifood.model.EarningsDelivery;
import com.mockifood.ifood.model.EarningsResponse;
import com.mockifood.ifood.model.EarningsSummary;
import com.mockifood.ifood.model.PerformanceResponse;
import com.mockifood.ifood.model.SummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class IfoodMockService {
  private static final String PLATFORM = "ifood";
  private final IfoodMockProperties props;

  public IfoodMockService(IfoodMockProperties props) {
    this.props = props;
  }

  public EarningsResponse buscarGanhos(String userId, LocalDate de, LocalDate ate, PerfilIfood perfilOverride) {
    var perfil = perfilOverride != null ? perfilOverride : identificarPerfil(userId);
    var rng = rng(userId, "earnings:" + perfil + ":" + de + ":" + ate);

    var entregas = gerarEntregas(perfil, de, ate, rng);
    var resumo = resumir(entregas);
    return new EarningsResponse(userId, PLATFORM, entregas, resumo);
  }

  public SummaryResponse buscarResumo(String userId, LocalDate de, LocalDate ate, PerfilIfood perfilOverride) {
    var ganhos = buscarGanhos(userId, de, ate, perfilOverride);
    var resumo = ganhos.resumo();

    long totalDiasNaJanela = Math.max(1, ChronoUnit.DAYS.between(de, ate) + 1);
    double consistencia = round2(Math.min(1.0, (double) resumo.diasTrabalhados() / (double) totalDiasNaJanela));

    return new SummaryResponse(
        ganhos.usuarioId(),
        ganhos.plataforma(),
        resumo.mediaDiaria(),
        consistencia,
        resumo.totalGanho(),
        resumo.diasTrabalhados()
    );
  }

  public PerformanceResponse buscarPerformance(String userId, PerfilIfood perfilOverride) {
    var perfil = perfilOverride != null ? perfilOverride : identificarPerfil(userId);
    var rng = rng(userId, "performance:" + perfil);

    double taxaCancelamento;
    double mediaAvaliacao;
    double taxaAceitacao;
    double taxaPontualidade;

    switch (perfil) {
      case A_ESTAVEL -> {
        taxaCancelamento = round2(0.01 + rng.nextDouble() * 0.03);
        mediaAvaliacao = round2(4.75 + rng.nextDouble() * 0.20);
        taxaAceitacao = round2(0.90 + rng.nextDouble() * 0.09);
        taxaPontualidade = round2(0.92 + rng.nextDouble() * 0.07);
      }
      case B_VARIAVEL -> {
        taxaCancelamento = round2(0.03 + rng.nextDouble() * 0.07);
        mediaAvaliacao = round2(4.30 + rng.nextDouble() * 0.40);
        taxaAceitacao = round2(0.75 + rng.nextDouble() * 0.20);
        taxaPontualidade = round2(0.82 + rng.nextDouble() * 0.12);
      }
      case C_IRREGULAR -> {
        taxaCancelamento = round2(0.08 + rng.nextDouble() * 0.18);
        mediaAvaliacao = round2(3.70 + rng.nextDouble() * 0.70);
        taxaAceitacao = round2(0.55 + rng.nextDouble() * 0.25);
        taxaPontualidade = round2(0.62 + rng.nextDouble() * 0.20);
      }
      default -> throw new IllegalStateException("Unexpected profile: " + perfil);
    }

    return new PerformanceResponse(userId, PLATFORM, taxaCancelamento, mediaAvaliacao, taxaAceitacao, taxaPontualidade);
  }

  public PerfilIfood identificarPerfil(String userId) {
    // deterministic mapping (good for tests / demo)
    var h = Math.abs(userId.hashCode());
    return switch (h % 3) {
      case 0 -> PerfilIfood.A_ESTAVEL;
      case 1 -> PerfilIfood.B_VARIAVEL;
      default -> PerfilIfood.C_IRREGULAR;
    };
  }

  private List<EarningsDelivery> gerarEntregas(PerfilIfood perfil, LocalDate de, LocalDate ate, Random rng) {
    var out = new ArrayList<EarningsDelivery>();
    for (var d = de; !d.isAfter(ate); d = d.plusDays(1)) {
      if (!trabalhouNoDia(perfil, d, rng)) continue;

      int horas = horasTrabalhadas(perfil, rng);
      int pedidos = quantidadePedidos(perfil, horas, rng);
      BigDecimal valor = valorGanho(perfil, pedidos, rng);

      out.add(new EarningsDelivery(d, valor, pedidos, horas));
    }
    return List.copyOf(out);
  }

  private boolean trabalhouNoDia(PerfilIfood perfil, LocalDate dia, Random rng) {
    // skew weekends a bit higher, like real gig work
    boolean fimDeSemana = dia.getDayOfWeek().getValue() >= 6;
    double base;
    switch (perfil) {
      case A_ESTAVEL -> base = fimDeSemana ? 0.92 : 0.80;
      case B_VARIAVEL -> base = fimDeSemana ? 0.72 : 0.48;
      case C_IRREGULAR -> base = fimDeSemana ? 0.45 : 0.22;
      default -> throw new IllegalStateException("Unexpected profile: " + perfil);
    }
    return rng.nextDouble() < base;
  }

  private int horasTrabalhadas(PerfilIfood perfil, Random rng) {
    return switch (perfil) {
      case A_ESTAVEL -> clampInt(5 + rng.nextInt(5), 4, 10);   // 5..9
      case B_VARIAVEL -> clampInt(3 + rng.nextInt(7), 1, 10); // 3..9
      case C_IRREGULAR -> clampInt(1 + rng.nextInt(6), 1, 8); // 1..6
    };
  }

  private int quantidadePedidos(PerfilIfood perfil, int horas, Random rng) {
    // simple: orders per hour
    double oph = switch (perfil) {
      case A_ESTAVEL -> 1.3 + rng.nextDouble() * 0.6;   // 1.3..1.9
      case B_VARIAVEL -> 0.9 + rng.nextDouble() * 1.0; // 0.9..1.9
      case C_IRREGULAR -> 0.6 + rng.nextDouble() * 0.8; // 0.6..1.4
    };
    int pedidos = (int) Math.round(horas * oph);
    return clampInt(pedidos, 1, 20);
  }

  private BigDecimal valorGanho(PerfilIfood perfil, int pedidos, Random rng) {
    // amount per order with noise
    BigDecimal porPedido = switch (perfil) {
      case A_ESTAVEL -> bd(14.5 + rng.nextDouble() * 3.5);   // 14.5..18.0
      case B_VARIAVEL -> bd(12.0 + rng.nextDouble() * 7.0); // 12.0..19.0
      case C_IRREGULAR -> bd(9.0 + rng.nextDouble() * 6.0); // 9.0..15.0
    };
    BigDecimal ruido = switch (perfil) {
      case A_ESTAVEL -> bd(0.95 + rng.nextDouble() * 0.10); // 0.95..1.05
      case B_VARIAVEL -> bd(0.80 + rng.nextDouble() * 0.40); // 0.80..1.20
      case C_IRREGULAR -> bd(0.70 + rng.nextDouble() * 0.55); // 0.70..1.25
    };
    return porPedido
        .multiply(BigDecimal.valueOf(pedidos))
        .multiply(ruido)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private EarningsSummary resumir(List<EarningsDelivery> entregas) {
    var total = entregas.stream()
        .map(EarningsDelivery::valor)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

    int dias = entregas.size();
    var media = dias == 0
        ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        : total.divide(BigDecimal.valueOf(dias), 2, RoundingMode.HALF_UP);

    return new EarningsSummary(total, media, dias);
  }

  private Random rng(String userId, String salt) {
    long base = props.semente();
    long h1 = userId == null ? 0 : userId.hashCode();
    long h2 = salt.hashCode();
    return new Random(base ^ (h1 * 31L) ^ (h2 * 131L));
  }

  private static BigDecimal bd(double v) {
    return BigDecimal.valueOf(v);
  }

  private static int clampInt(int v, int min, int max) {
    return Math.max(min, Math.min(max, v));
  }

  private static double round2(double v) {
    return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}

