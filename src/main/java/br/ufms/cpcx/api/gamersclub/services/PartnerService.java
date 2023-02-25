package br.ufms.cpcx.api.gamersclub.services;

import br.ufms.cpcx.api.gamersclub.exceptions.AlreadyExistsException;
import br.ufms.cpcx.api.gamersclub.exceptions.PartnerWithoutGameException;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import br.ufms.cpcx.api.gamersclub.repositories.PartnerRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
class PartnerService {

    private final PartnerRepository partnerRepository;
    private final GameService gameService;

    private final GameLoanService gameLoanService;

    public PartnerService(PartnerRepository partnerRepository, GameService gameService, GameLoanService gameLoanService) {
        this.partnerRepository = partnerRepository;
        this.gameService = gameService;
        this.gameLoanService = gameLoanService;
    }

    @Transactional
    public PartnerModel save(PartnerModel partnerModel) throws Exception {
        if (partnerRepository.existsByNameAndPhoneNumber(partnerModel.getName(), partnerModel.getPhoneNumber())) {
            throw new AlreadyExistsException("This Partner already exists!");
        }
        if (CollectionUtils.isEmpty(partnerModel.getGames())){
            throw new PartnerWithoutGameException("To register a member it's necessary to register at least one game.");
        }
        partnerRepository.save(partnerModel);
        for (GameModel gameModel: partnerModel.getGames() ) {
            gameModel.setOwner(partnerModel);
            gameService.save(gameModel);
        }
        return partnerModel;
    }

    @Transactional
    public PartnerModel update(PartnerModel partnerModel) throws Exception {
        partnerRepository.save(partnerModel);
        return partnerModel;
    }

    /**
     Deletes a member partner and all their games (by cascade database)
     @param partnerModel
     */
    @Transactional
    public void delete(PartnerModel partnerModel) {
        //VERIFICACAAO PARA VER SE NAO TEM JOGO ALUGADO NO NOME DELE
        if (this.gameLoanService.checkLoan(partnerModel.getId())){
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "There is a pending loan, check!");
        }

        partnerRepository.delete(partnerModel);
    }

    public Page<PartnerModel> findAll(Pageable pageable) {
        return partnerRepository.findAll(pageable);
    }

    public Optional<PartnerModel> findById(Long id) {
        return partnerRepository.findById(id);
    }

    public boolean existsByNameAndPhoneNumber(String name, String phoneNumber) {
        return partnerRepository.existsByNameAndPhoneNumber(name, phoneNumber);
    }

    /**
     * partners by name: {name with statrtsWith and ignorecase}
     * @param name
     * @param pageable
     * @return
     */
    public Page<PartnerModel> findByFilter(String name, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.startsWith())
                .withIgnoreCase()
                .withIgnoreNullValues();

        PartnerModel partnerModel = new PartnerModel();
        partnerModel.setName(name);
        Example<PartnerModel> example = Example.of(partnerModel, matcher);
        return partnerRepository.findAll(example, pageable);
    }

}
