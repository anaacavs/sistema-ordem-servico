package com.exemplo.ordemservico.service;

import com.exemplo.ordemservico.model.Cliente;
import com.exemplo.ordemservico.repository.ClienteRepository;
import com.exemplo.ordemservico.repository.OrdemServicoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          OrdemServicoRepository ordemServicoRepository) {
        this.clienteRepository = clienteRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id));
    }

    // REGRA 1: não pode existir dois clientes com o mesmo CPF.
    public Cliente salvar(Cliente cliente) {
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Já existe cliente com este CPF");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        // REGRA 9 (exercício): cliente com OS não pode ser excluído.
        if (ordemServicoRepository.existsByClienteId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Não é possível excluir cliente com ordem de serviço");
        }
        clienteRepository.delete(cliente);
    }
}
