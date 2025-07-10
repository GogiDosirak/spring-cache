package com.sprint.springcache.controller;

import com.sprint.springcache.service.MemoryMonitoringService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memory")
public class MemoryMonitoringController {
  private final MemoryMonitoringService memoryMonitoringService;

  @GetMapping
  public Map<String, Object> getMemoryStats() {
    return memoryMonitoringService.getMemoryStats();
  }

}
