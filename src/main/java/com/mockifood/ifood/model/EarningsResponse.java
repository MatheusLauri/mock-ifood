package com.mockifood.ifood.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record EarningsResponse(
    @JsonProperty("userId") String usuarioId,
    @JsonProperty("platform") String plataforma,
    @JsonProperty("deliveries") List<EarningsDelivery> entregas,
    @JsonProperty("summary") EarningsSummary resumo
) {}

