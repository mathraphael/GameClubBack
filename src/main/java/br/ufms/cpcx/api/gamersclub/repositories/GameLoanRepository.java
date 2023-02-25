package br.ufms.cpcx.api.gamersclub.repositories;

import br.ufms.cpcx.api.gamersclub.models.GameLoanModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLoanRepository extends CrudRepository<GameLoanModel, Long> {
    List<GameLoanModel> findAllByPartnerId(Long id);
    boolean existsByPartnerId(Long id);
    boolean existsByGameId(Long id);
}