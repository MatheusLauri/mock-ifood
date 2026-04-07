package com.mockifood.ifood.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EarningsDelivery(
    @JsonProperty("date") LocalDate data,
    @JsonProperty("amount") BigDecimal valor,
    @JsonProperty("orders") int pedidos,
    @JsonProperty("hoursWorked") int horasTrabalhadas
) {}

