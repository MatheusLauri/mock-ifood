package com.mockifood.ifood.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record SummaryResponse(
    @JsonProperty("userId") String usuarioId,
    @JsonProperty("platform") String plataforma,
    @JsonProperty("avgDaily") BigDecimal mediaDiaria,
    @JsonProperty("consistency") double consistencia,
    @JsonProperty("totalEarned") BigDecimal totalGanho,
    @JsonProperty("daysWorked") int diasTrabalhados
) {}

