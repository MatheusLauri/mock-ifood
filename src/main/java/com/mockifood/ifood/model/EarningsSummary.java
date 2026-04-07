package com.mockifood.ifood.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record EarningsSummary(
    @JsonProperty("totalEarned") BigDecimal totalGanho,
    @JsonProperty("avgDaily") BigDecimal mediaDiaria,
    @JsonProperty("daysWorked") int diasTrabalhados
) {}

