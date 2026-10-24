package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.DashboardControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.PeriodosHistoricoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@RestController
@RequestMapping("/dashboard")
public class DashboardController implements DashboardControllerContract {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<DashboardResDTO> consultar(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesReferencia
    ) {
        return ResponseEntity.ok(service.consultar(mesReferencia));
    }

    @GetMapping("/periodos-com-historico")
    @Override
    public ResponseEntity<PeriodosHistoricoResDTO> consultarPeriodosComHistorico() {
        return ResponseEntity.ok(service.consultarPeriodosComHistorico());
    }
}
