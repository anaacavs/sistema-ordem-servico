package com.exemplo.ordemservico.service;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.Cliente;
import com.exemplo.ordemservico.model.OrdemServico;
import com.exemplo.ordemservico.model.Tecnico;
import com.exemplo.ordemservico.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdemServicoService {
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteService clienteService;
    private final TecnicoService tecnicoService;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               ClienteService clienteService,
                               TecnicoService tecnicoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteService = clienteService;
        this.tecnicoService = tecnicoService;
    }

    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAll();
    }

    public List<OrdemServico> listarPorStatus(StatusOS status) {
        return ordemServicoRepository.findByStatus(status);
    }

    public List<OrdemServico> listarPorPrioridade(Prioridade prioridade) {
        return ordemServicoRepository.findByPrioridade(prioridade);
    }

    public List<OrdemServico> listarPorCliente(Long clienteId) {
        return ordemServicoRepository.findByClienteId(clienteId);
    }

    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ordem de serviço não encontrada: " + id));
    }

    // REGRA 2: a OS só pode ser aberta para um cliente já cadastrado.
    private Cliente validarClienteCadastrado(OrdemServico os) {
        if (os.getCliente() == null || os.getCliente().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do cliente");
        }
        return clienteService.buscarPorId(os.getCliente().getId());
    }

    // REGRA 3: ao abrir, define a data e o status inicial.
    public OrdemServico abrir(OrdemServico os) {
        Cliente cliente = validarClienteCadastrado(os);
        os.setCliente(cliente);
        os.setDataAbertura(LocalDateTime.now());
        os.setStatus(StatusOS.ABERTA);
        os.setTecnico(null);
        os.setDataFechamento(null);
        os.setValor(null);
        return ordemServicoRepository.save(os);
    }

    // REGRA 4: OS concluída ou cancelada não pode mais ser alterada.
    private void validarOsNaoFinalizada(OrdemServico os) {
        if (os.getStatus() == StatusOS.CONCLUIDA || os.getStatus() == StatusOS.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A OS " + os.getId() + " já está " + os.getStatus());
        }
    }

    // REGRA 5: atribuir técnico muda a OS para EM_ANDAMENTO.
    public OrdemServico atribuirTecnico(Long osId, Long tecnicoId) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os);
        Tecnico tecnico = tecnicoService.buscarPorId(tecnicoId);
        os.setTecnico(tecnico);
        os.setStatus(StatusOS.EM_ANDAMENTO);
        return ordemServicoRepository.save(os);
    }

    // REGRA 6: só conclui uma OS que tenha técnico atribuído.
    private void validarTecnicoAtribuido(OrdemServico os) {
        if (os.getTecnico() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Atribua um técnico antes de concluir");
        }
    }

    // REGRA 10 (exercício): o valor informado deve ser maior que zero.
    private void validarValorPositivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O valor da conclusão deve ser maior que zero");
        }
    }

    // REGRA 7: grava valor, data de fechamento e status CONCLUIDA.
    public OrdemServico concluir(Long osId, BigDecimal valor) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os);
        validarTecnicoAtribuido(os);
        validarValorPositivo(valor);
        os.setValor(valor);
        os.setDataFechamento(LocalDateTime.now());
        os.setStatus(StatusOS.CONCLUIDA);
        return ordemServicoRepository.save(os);
    }

    // REGRA 8: grava data de fechamento e status CANCELADA.
    public OrdemServico cancelar(Long osId) {
        OrdemServico os = buscarPorId(osId);
        validarOsNaoFinalizada(os);
        os.setDataFechamento(LocalDateTime.now());
        os.setStatus(StatusOS.CANCELADA);
        return ordemServicoRepository.save(os);
    }
}
