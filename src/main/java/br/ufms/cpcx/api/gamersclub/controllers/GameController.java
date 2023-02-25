package br.ufms.cpcx.api.gamersclub.controllers;

import br.ufms.cpcx.api.gamersclub.dtos.GameDto;
import br.ufms.cpcx.api.gamersclub.models.ConsoleEnum;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import br.ufms.cpcx.api.gamersclub.models.assembler.GameRepresentationModelAssembler;
import br.ufms.cpcx.api.gamersclub.services.GameClubFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/v2/game")
public class GameController {
    final GameClubFacade gameClubFacade;
    final GameRepresentationModelAssembler gameModelAssembler;
    final PagedResourcesAssembler pagedResourcesAssembler;
    final Pageable pageable = PageRequest.of(0, 10);

    public GameController(GameClubFacade gameClubFacade, GameRepresentationModelAssembler gameModelAssembler, PagedResourcesAssembler pagedResourcesAssembler) {
        this.gameClubFacade = gameClubFacade;
        this.gameModelAssembler = gameModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }


    @PostMapping
    public ResponseEntity<Object> saveGame(@RequestBody @Valid GameDto gameDto) {
        var gameModel = new GameModel();
        BeanUtils.copyProperties(gameDto, gameModel);

        gameModel = gameClubFacade.gameSave(gameModel);
        EntityModel<GameModel> entityModel = gameModelAssembler.toModel(gameModel)
                .add(linkTo(methodOn(GameController.class).getAllGames(pageable)).withRel("all games"));

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GameModel>> getGameById(@PathVariable(value = "id") Long id) {
        return gameClubFacade.gameFindById(id)
                .map(gameModel -> {
                    EntityModel<GameModel> game = gameModelAssembler.toModel(gameModel)
                            .add(linkTo(methodOn(GameController.class).getAllGames(pageable)).withRel("all games"));
                    return ResponseEntity.ok(game);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGame(@PathVariable(value = "id") Long id) {
        Optional<GameModel> gameModelOptional = gameClubFacade.gameFindById(id);
        if (!gameModelOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found.");
        }
        gameClubFacade.gameDelete(gameModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Game deleted successfully.");
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GameModel>> updateGame(@PathVariable(value = "id") Long id,
                                             @RequestBody @Valid GameDto gameDto, Errors errors) {

        if (errors.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.toString());
        }

        Optional<GameModel> gameModelOptional = gameClubFacade.gameFindById(id);
        if (!gameModelOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found.");
        }

        var gameModel = new GameModel();
        BeanUtils.copyProperties(gameDto, gameModel);
        gameModel.setId(gameModelOptional.get().getId());
        gameModel.setOwner(gameModelOptional.get().getOwner());

        EntityModel<GameModel> game = gameModelAssembler.toModel(gameClubFacade.gameUpdate(gameModel));

        return ResponseEntity.ok(game);
    }


    @GetMapping
    public ResponseEntity<PagedModel<GameModel>> getAllGames(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var page = gameClubFacade.gameFindAll(pageable);
        PagedModel<GameModel> collModel = pagedResourcesAssembler.toModel(page, gameModelAssembler);
        return new ResponseEntity<>(collModel, HttpStatus.OK);
    }

    @GetMapping("/search/{console}")
    public ResponseEntity<PagedModel<GameModel>> findGamesByConsoleAndFilters(@PathVariable ConsoleEnum console,
                                                                        @RequestParam(required = false) String name,
                                                                        @RequestParam(required = false) String owner,
                                                                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        var gameDto = new GameDto();
        gameDto.setConsole(console);
        gameDto.setName(name);

        var partner = new PartnerModel();
        partner.setName(owner);
        gameDto.setOwner(partner);

        var page = gameClubFacade.gameFindByConsoleAndFilter(gameDto, pageable);
        PagedModel<GameModel> collModel = pagedResourcesAssembler.toModel(page, gameModelAssembler);

        return new ResponseEntity<>(collModel, HttpStatus.OK);
    }


}
