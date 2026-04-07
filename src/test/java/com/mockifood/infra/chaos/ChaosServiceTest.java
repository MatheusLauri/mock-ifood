package com.mockifood.infra.chaos;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mockifood.ifood.service.IfoodMockProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class ChaosServiceTest {

  @Test
  void maybeChaos_quandoDesabilitado_naoDeveFalhar() {
    var props = new IfoodMockProperties(
        42,
        new IfoodMockProperties.Caos(false, 0, 0, 1.0, 1.0, 1)
    );
    var service = new ChaosService(props);

    service.maybeChaos("123", "earnings");
  }

  @Test
  void maybeChaos_quandoErroRate1_deveLancarResponseStatusException() {
    var props = new IfoodMockProperties(
        42,
        new IfoodMockProperties.Caos(true, 0, 0, 1.0, 0.0, 0)
    );
    var service = new ChaosService(props);

    assertThatThrownBy(() -> service.maybeChaos("123", "earnings"))
        .isInstanceOf(ResponseStatusException.class);
  }
}

