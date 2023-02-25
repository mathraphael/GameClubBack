package br.ufms.cpcx.api.gamersclub.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardErrorMessageDto {

    private Date timestamp;
    private int status;
    private String message;


}
