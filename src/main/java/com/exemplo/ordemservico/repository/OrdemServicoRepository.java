package com.exemplo.ordemservico.repository;

import com.exemplo.ordemservico.enums.Prioridade;
import com.exemplo.ordemservico.enums.StatusOS;
import com.exemplo.ordemservico.model.OrdemServico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByStatus(StatusOS status);
    List<OrdemServico> findByClienteId(Long clienteId);
    List<OrdemServico> findByPrioridade(Prioridade prioridade);
    boolean existsByClienteId(Long clienteId);
}
