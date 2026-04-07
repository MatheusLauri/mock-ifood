package com.mockifood.infra.chaos;

import com.mockifood.ifood.service.IfoodMockProperties;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChaosService {
  private final IfoodMockProperties props;

  public ChaosService(IfoodMockProperties props) {
    this.props = props;
  }

  public void maybeChaos(String userId, String operation) {
    var caos = props.caos();
    if (caos == null || !caos.enabled()) {
      return;
    }

    var rng = new Random(props.semente() ^ (userId == null ? 0 : userId.hashCode()) ^ operation.hashCode());

    int min = Math.max(0, caos.delayMsMin());
    int max = Math.max(min, caos.delayMsMax());
    int delay = min + rng.nextInt(max - min + 1);
    sleep(delay);

    if (rng.nextDouble() < caos.timeoutRate()) {
      sleep(Math.max(0, caos.timeoutMs()));
    }

    if (rng.nextDouble() < caos.errorRate()) {
      var status = rng.nextBoolean() ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(status, status == HttpStatus.SERVICE_UNAVAILABLE ? "IFood_UPSTREAM_503" : "IFood_UPSTREAM_500");
    }
  }

  private static void sleep(int ms) {
    if (ms <= 0) return;
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

