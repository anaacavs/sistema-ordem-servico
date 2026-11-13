package com.exemplo.ordemservico.controller;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.OrdemServico;
import com.exemplo.ordemservico.service.OrdemServicoService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordens")
public class OrdemServicoController {
    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping
    public List<OrdemServico> listar(@RequestParam(name = "status", required = false) StatusOS status) {
        return status == null
                ? ordemServicoService.listarTodas()
                : ordemServicoService.listarPorStatus(status);
    }

    @GetMapping("/{id}")
    public OrdemServico buscar(@PathVariable("id") Long id) {
        return ordemServicoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<OrdemServico> listarPorCliente(@PathVariable("clienteId") Long clienteId) {
        return ordemServicoService.listarPorCliente(clienteId);
    }

    // Exercício: consultar ordens por prioridade.
    @GetMapping("/prioridade/{prioridade}")
    public List<OrdemServico> listarPorPrioridade(@PathVariable("prioridade") Prioridade prioridade) {
        return ordemServicoService.listarPorPrioridade(prioridade);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdemServico abrir(@RequestBody OrdemServico os) {
        return ordemServicoService.abrir(os);
    }

    @PutMapping("/{id}/tecnico/{tecnicoId}")
    public OrdemServico atribuirTecnico(@PathVariable("id") Long id,
                                        @PathVariable("tecnicoId") Long tecnicoId) {
        return ordemServicoService.atribuirTecnico(id, tecnicoId);
    }

    @PutMapping("/{id}/concluir")
    public OrdemServico concluir(@PathVariable("id") Long id,
                                 @RequestParam(name = "valor", required = false) BigDecimal valor) {
        return ordemServicoService.concluir(id, valor);
    }

    @PutMapping("/{id}/cancelar")
    public OrdemServico cancelar(@PathVariable("id") Long id) {
        return ordemServicoService.cancelar(id);
    }
}
