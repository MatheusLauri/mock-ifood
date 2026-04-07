package com.mockifood.ifood.model;

public record PerformanceResponse(
    @com.fasterxml.jackson.annotation.JsonProperty("userId") String usuarioId,
    @com.fasterxml.jackson.annotation.JsonProperty("platform") String plataforma,
    @com.fasterxml.jackson.annotation.JsonProperty("cancellationRate") double taxaCancelamento,
    @com.fasterxml.jackson.annotation.JsonProperty("averageRating") double mediaAvaliacao,
    @com.fasterxml.jackson.annotation.JsonProperty("acceptanceRate") double taxaAceitacao,
    @com.fasterxml.jackson.annotation.JsonProperty("onTimeRate") double taxaPontualidade
) {}

