package br.ufms.cpcx.api.gamersclub.services;

import br.ufms.cpcx.api.gamersclub.dtos.GameDto;
import br.ufms.cpcx.api.gamersclub.models.ConsoleEnum;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import br.ufms.cpcx.api.gamersclub.repositories.GameLoanRepository;
import br.ufms.cpcx.api.gamersclub.repositories.GameRepository;
import br.ufms.cpcx.api.gamersclub.repositories.PartnerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
class GameService {

    final GameRepository gameRepository;

    //nao deveria ter acesso ao partner repository, somente ao PartnerServices
    final PartnerRepository partnerRepository;
    final GameLoanRepository gameLoanRepository;

    public GameService(GameRepository gameRepository, PartnerRepository partnerRepository, GameLoanRepository gameLoanRepository) {
        this.gameRepository = gameRepository;
        this.partnerRepository = partnerRepository;
        this.gameLoanRepository = gameLoanRepository;
    }

    @Transactional
    public GameModel save(GameModel gameModel) {
        if (gameRepository.existsByNameAndConsoleAndOwner(gameModel.getName(), gameModel.getConsole(), gameModel.getOwner())) {
            throw new ResponseStatusException( HttpStatus.CONFLICT, "This game already exists!");
        }

       return gameRepository.save(gameModel) ;
    }

    @Transactional
    public GameModel update(GameModel gameModel) {

        return gameRepository.save(gameModel);
    }
    @Transactional
    public void delete(GameModel gameModel) {
        if (this.gameLoanRepository.existsByGameId(gameModel.getId())){
            throw new ResponseStatusException( HttpStatus.CONFLICT, "It is not allowed to delete the game because it is loaned!");

        }
        gameRepository.delete(gameModel);
    }

    public Page<GameModel> findAll(Pageable pageable) {
        return gameRepository.findAll(pageable);
    }

    public Optional<GameModel> findById(Long id) {
        return gameRepository.findById(id);
    }

    public boolean existsByNameAndConsoleAndOwner(String name, ConsoleEnum consoleEnum, PartnerModel owner) {
        return gameRepository.existsByNameAndConsoleAndOwner(name, consoleEnum, owner);
    }

    /***
     * Returns games by console and optional ignore case: {name with statrtsWith and owner.name with contains str}
     * @param gameDto
     * @param pageable
     * @return
     */
    public Page<GameModel> findByConsoleAndFilter(GameDto gameDto, Pageable pageable) {
        var gameModel = new GameModel();
        BeanUtils.copyProperties(gameDto, gameModel);

        var matcher = ExampleMatcher.matching()
                .withMatcher("console", match -> match.exact())
                .withMatcher("name", match -> match.startsWith())
                .withMatcher("owner.name", match -> match.contains())
                .withIgnoreCase()
                .withIgnoreNullValues();

        var example = Example.of(gameModel, matcher);

        return gameRepository.findAll(example, pageable);
    }

    public Page<GameModel> findByOwnerId(Long ownerId, Pageable pageable) {
        var matcher = ExampleMatcher.matching()
                .withMatcher("owner.id", match -> match.exact())
                .withIgnoreCase()
                .withIgnoreNullValues();

        var gameModel = new GameModel();

        var partnerModel = new PartnerModel();
        partnerModel.setId(ownerId);

        gameModel.setOwner(partnerModel);

        var example = Example.of(gameModel, matcher);

        return gameRepository.findAll(example, pageable);
    }
}
