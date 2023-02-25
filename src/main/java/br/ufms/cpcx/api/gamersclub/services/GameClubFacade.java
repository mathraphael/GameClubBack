package br.ufms.cpcx.api.gamersclub.services;

import br.ufms.cpcx.api.gamersclub.dtos.GameDto;
import br.ufms.cpcx.api.gamersclub.models.ConsoleEnum;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameClubFacade {

    final GameService gameService;
    final PartnerService partnerService;

    public GameClubFacade(GameService gameService, PartnerService partnerService) {
        this.gameService = gameService;
        this.partnerService = partnerService;
    }

    // Game
    public boolean gameExistsByNameAndConsoleAndOwner(String name, ConsoleEnum console, PartnerModel owner) {
        return gameService.existsByNameAndConsoleAndOwner(name, console, owner);
    }

    public GameModel gameSave(GameModel gameModel) {
        return gameService.save(gameModel);
    }
    public GameModel gameUpdate(GameModel gameModel) {
        return gameService.update(gameModel);
    }
    public Optional<GameModel> gameFindById(Long id) {
        return gameService.findById(id);
    }

    /**
     * Deletes the game and if it is the last one, also deletes the partner
     * @param gameModel
     */
    public void gameDelete(GameModel gameModel) {
        var partner = partnerService.findById(gameModel.getOwner().getId());
        if (partner.isPresent()) {
            if( partner.get().getGames().size() > 1){
                gameService.delete(gameModel);
            }
            else {
                partnerService.delete(partner.get());
            }
        }
    }

    public Page<GameModel> gameFindAll(Pageable pageable) {
        return gameService.findAll(pageable);
    }

    public Page<GameModel> gameFindByConsoleAndFilter(GameDto gameDto, Pageable pageable) {
        return gameService.findByConsoleAndFilter(gameDto, pageable);
    }

    public Page<GameModel> gameFindByOwnerId(Long ownerId, Pageable pageable) {
        return gameService.findByOwnerId(ownerId, pageable);
    }

    // Partner


    public PartnerModel partnerSave(PartnerModel partnerModel) throws Exception {
        return partnerService.save(partnerModel);
    }

    public PartnerModel partnerUpdate(PartnerModel partnerModel) throws Exception {
        return partnerService.update(partnerModel);
    }

    public Optional<PartnerModel> partnerFindById(Long id) {
        return partnerService.findById(id);
    }


    public void partnerDelete(PartnerModel partnerModel) {
        // se outras regras se aplicam implementar aqui
        partnerService.delete(partnerModel);

    }

    public Page<PartnerModel> partnerFindAll(Pageable pageable) {
        return partnerService.findAll(pageable);
    }

    public Page<PartnerModel> partnerFindByFilter(String name, Pageable pageable) {
        return partnerService.findByFilter(name,pageable);
    }
}
