package br.ufms.cpcx.api.gamersclub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "TB_GAMELOAN")
@JsonPropertyOrder({"id","loanDate","scheduledReturnDate", "returnDate"})
public class GameLoanModel {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @OneToOne
    private PartnerModel partner;

    @OneToOne
    @JsonIgnore
    private GameModel game;

    //data de agora
    @Column(nullable = false, length = 15)
    private LocalDate loanDate;

    //data prevista
    private LocalDate scheduledReturnDate;

    //dia que realmente retornou
    private LocalDate returnDate;


}
