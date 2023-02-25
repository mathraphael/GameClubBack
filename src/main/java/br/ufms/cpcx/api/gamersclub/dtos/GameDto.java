package br.ufms.cpcx.api.gamersclub.dtos;

import br.ufms.cpcx.api.gamersclub.models.ConsoleEnum;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class GameDto {

    @NotBlank
    @Size(max = 100)
    private String name;
    private ConsoleEnum console;
    private PartnerModel owner;
}
