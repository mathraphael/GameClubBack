package br.ufms.cpcx.api.gamersclub.models.assembler;

import br.ufms.cpcx.api.gamersclub.controllers.GameController;
import br.ufms.cpcx.api.gamersclub.controllers.PartnerController;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameRepresentationModelAssembler implements SimpleRepresentationModelAssembler<GameModel> {

    @Override
    public void addLinks(EntityModel<GameModel> resource) {
        Long id = resource.getContent().getId();
        Long partnerId = resource.getContent().getOwner().getId();
        resource.add(linkTo(methodOn(GameController.class).getGameById(id)).withSelfRel());
        resource.add(linkTo(methodOn(PartnerController.class).getPartnerById(partnerId)).withRel("owner"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<GameModel>> resources) {
        var pageable = PageRequest.of(0,10);
        resources.add(linkTo(methodOn(GameController.class).getAllGames(pageable)).withSelfRel());
    }


}
