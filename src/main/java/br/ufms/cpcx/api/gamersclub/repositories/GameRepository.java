package br.ufms.cpcx.api.gamersclub.repositories;

import br.ufms.cpcx.api.gamersclub.models.ConsoleEnum;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface GameRepository  extends JpaRepository <GameModel, Long> {

    boolean existsByNameAndConsoleAndOwner(String name, ConsoleEnum consoleEnum, PartnerModel owner);

}
