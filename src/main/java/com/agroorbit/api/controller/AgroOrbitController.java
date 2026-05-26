package com.agroorbit.api.controller;

import com.agroorbit.api.dto.AlertaDTO;
import com.agroorbit.api.dto.AnaliseResponseDTO;
import com.agroorbit.api.dto.DashboardDTO;
import com.agroorbit.api.dto.FazendaRequestDTO;
import com.agroorbit.api.service.MonitoramentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "AgroOrbit API", description = "Endpoints de monitoramento agrícola via satélite")
public class AgroOrbitController {

    private final MonitoramentoService monitoramentoService;

    @PostMapping("/analise")
    @Operation(
        summary = "Analisar dados de satélite de uma fazenda",
        description = """
            Recebe os dados da fazenda e leituras do satélite (NDVI, temperatura, umidade)
            e retorna:
            - Nível de risco (NORMAL, ALERTA, CRITICO)
            - Score de risco de 0 a 100
            - Recomendação de ação para o fazendeiro
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Análise realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Fazenda já cadastrada com este email")
    })
    public ResponseEntity<AnaliseResponseDTO> analisar(
            @Valid @RequestBody FazendaRequestDTO request) {

        log.info("POST /analise — fazenda: {}", request.getEmail());
        AnaliseResponseDTO response = monitoramentoService.analisar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/dashboard")
    @Operation(
        summary = "Dashboard geral de monitoramento",
        description = """
            Retorna métricas agregadas:
            - Total de fazendas monitoradas
            - Distribuição por nível de risco
            - Média do índice NDVI por estado
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard gerado com sucesso")
    })
    public ResponseEntity<DashboardDTO> getDashboard() {
        log.info("GET /dashboard");
        return ResponseEntity.ok(monitoramentoService.getDashboard());
    }

    @GetMapping("/alertas")
    @Operation(
        summary = "Listar fazendas em situação de risco",
        description = """
            Retorna fazendas com score de risco acima do mínimo informado,
            ordenadas por prioridade (maior risco primeiro).
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista gerada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetro scoreMinimo inválido")
    })
    public ResponseEntity<List<AlertaDTO>> getAlertas(
            @Parameter(description = "Score mínimo de risco (0-100)", example = "50")
            @RequestParam(defaultValue = "50") int scoreMinimo) {

        log.info("GET /alertas — scoreMinimo: {}", scoreMinimo);
        return ResponseEntity.ok(monitoramentoService.getAlertas(scoreMinimo));
    }
}
