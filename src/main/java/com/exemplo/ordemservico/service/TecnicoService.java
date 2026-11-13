package com.exemplo.ordemservico.service;

import com.exemplo.ordemservico.model.Tecnico;
import com.exemplo.ordemservico.repository.TecnicoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TecnicoService {
    private final TecnicoRepository tecnicoRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository) {
        this.tecnicoRepository = tecnicoRepository;
    }

    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAll();
    }

    public Tecnico buscarPorId(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Técnico não encontrado: " + id));
    }

    public Tecnico salvar(Tecnico tecnico) {
        return tecnicoRepository.save(tecnico);
    }

    public Tecnico atualizar(Long id, Tecnico dados) {
        Tecnico tecnico = buscarPorId(id);
        tecnico.setNome(dados.getNome());
        tecnico.setEspecialidade(dados.getEspecialidade());
        tecnico.setTelefone(dados.getTelefone());
        return tecnicoRepository.save(tecnico);
    }

    public void excluir(Long id) {
        tecnicoRepository.delete(buscarPorId(id));
    }
}
