package br.ufms.cpcx.api.gamersclub.controllers;


import br.ufms.cpcx.api.gamersclub.dtos.GameDto;
import br.ufms.cpcx.api.gamersclub.dtos.PartnerDto;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import br.ufms.cpcx.api.gamersclub.services.GameClubFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v2/partner")
public class PartnerController {
    final GameClubFacade gameClubFacade;

    public PartnerController(GameClubFacade gameClubFacade) {
        this.gameClubFacade = gameClubFacade;
    }


    @PostMapping
    public ResponseEntity<Object> savePartner(@RequestBody PartnerDto partnerDto) throws Exception {
        PartnerModel partnerModel = new PartnerModel();
        BeanUtils.copyProperties(partnerDto, partnerModel);
        if (!CollectionUtils.isEmpty(partnerDto.getGames())){
            // copy elements list
            partnerModel.setGames(new ArrayList<>());
            for (GameDto gameDto : partnerDto.getGames()) {
                GameModel gameModel = new GameModel();
                gameModel.setOwner(partnerModel);
                BeanUtils.copyProperties(gameDto, gameModel);
                partnerModel.getGames().add(gameModel);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(gameClubFacade.partnerSave(partnerModel));
    }

    /**
     * NOTE: Atualiza dados do Partner, nao dados de games
     * @param id
     * @param partnerDto
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePartner(@PathVariable(value = "id") Long id,
                                                @RequestBody @Valid PartnerDto partnerDto) throws Exception {
        Optional<PartnerModel> partnerModelOptional = gameClubFacade.partnerFindById(id);
        if (!partnerModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found.");
        }
        var partnerModel = new PartnerModel();
        BeanUtils.copyProperties(partnerDto, partnerModel);
        partnerModel.setId(partnerModelOptional.get().getId());

        return ResponseEntity.status(HttpStatus.OK).body(gameClubFacade.partnerUpdate(partnerModel));
    }




    @GetMapping("/{id}")
    public ResponseEntity<Object> getPartnerById(@PathVariable(value = "id") Long id) {
        Optional<PartnerModel> partnerModelOptional = gameClubFacade.partnerFindById(id);
        if (!partnerModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(partnerModelOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePartner(@PathVariable(value = "id") Long id) {
        Optional<PartnerModel> partnerModelOptional = gameClubFacade.partnerFindById(id);
        if (!partnerModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partner not found.");
        }
        gameClubFacade.partnerDelete(partnerModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Partner deleted successfully.");
    }



    @GetMapping
    public ResponseEntity<Object> getAllPartners(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(gameClubFacade.partnerFindAll(pageable));
    }


    @GetMapping(value = "/search/")
    public ResponseEntity<Page<PartnerModel>> findPartnersByFilters(@RequestParam String name,
                                                                    @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

       return ResponseEntity.status(HttpStatus.OK).body(gameClubFacade.partnerFindByFilter(name, pageable));
    }

    @GetMapping("/{id}/games")
    public ResponseEntity<Page<GameModel>> findGamesByOwnerId(@PathVariable Long id,
                                                     @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Optional<PartnerModel> partnerModelOptional = gameClubFacade.partnerFindById(id);
        if (!partnerModelOptional.isPresent()) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Partner not found.");
        }
        //return ResponseEntity.status(HttpStatus.OK).body(partnerModelOptional.get().getGames()); sem paginacao!
        return ResponseEntity.status(HttpStatus.OK).body(gameClubFacade.gameFindByOwnerId(id, pageable));
    }


}
