package com.mockifood.controller;

import com.mockifood.ifood.model.EarningsResponse;
import com.mockifood.ifood.model.PerformanceResponse;
import com.mockifood.ifood.model.SummaryResponse;
import com.mockifood.infra.chaos.ChaosService;
import com.mockifood.ifood.service.PerfilIfood;
import com.mockifood.ifood.service.IfoodMockService;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ifood")
public class IfoodController {
  private final IfoodMockService service;
  private final ChaosService chaos;

  public IfoodController(IfoodMockService service, ChaosService chaos) {
    this.service = service;
    this.chaos = chaos;
  }

  @GetMapping("/earnings/{userId}")
  public EarningsResponse earnings(
      @PathVariable String userId,
      @RequestParam Optional<PerfilIfood> profile,
      @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
      @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to
  ) {
    chaos.maybeChaos(userId, "earnings");
    var end = to.orElse(LocalDate.now());
    var start = from.orElse(end.minusDays(29));
    return service.buscarGanhos(userId, start, end, profile.orElse(null));
  }

  @GetMapping("/performance/{userId}")
  public PerformanceResponse performance(
      @PathVariable String userId,
      @RequestParam Optional<PerfilIfood> profile
  ) {
    chaos.maybeChaos(userId, "performance");
    return service.buscarPerformance(userId, profile.orElse(null));
  }

  @GetMapping("/summary/{userId}")
  public SummaryResponse summary(
      @PathVariable String userId,
      @RequestParam Optional<PerfilIfood> profile,
      @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
      @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to
  ) {
    chaos.maybeChaos(userId, "summary");
    var end = to.orElse(LocalDate.now());
    var start = from.orElse(end.minusDays(29));
    return service.buscarResumo(userId, start, end, profile.orElse(null));
  }
}

