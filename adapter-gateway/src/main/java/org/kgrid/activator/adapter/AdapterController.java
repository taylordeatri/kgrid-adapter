package org.kgrid.activator.adapter;

import java.util.List;
import org.kgrid.activator.adapter.api.Adapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdapterController {

  private final Adapter adapter;

  public AdapterController(Adapter adapter) {

    this.adapter = adapter;
  }

  @GetMapping("/types")
  public List<String> types() {

    return adapter.getTypes();
  }

  @GetMapping("/status")
  public String status() {

    return adapter.status();
  }

  @GetMapping("/supports/{type}")
  public boolean supports(@PathVariable String type) {

    return adapter.supports(type);
  }

}
