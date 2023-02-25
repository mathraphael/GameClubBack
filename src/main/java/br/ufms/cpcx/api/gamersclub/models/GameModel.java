package br.ufms.cpcx.api.gamersclub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(
        name = "TB_GAME", uniqueConstraints = {@UniqueConstraint(name = "TB_GAME_UQ", columnNames = {"name", "console", "partner"})}
)
@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "console","name", "owner"})
@Relation(collectionRelation = "games", itemRelation = "game")
public class GameModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConsoleEnum console;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner", foreignKey = @ForeignKey(name = "FK_PARTNER"), nullable = false)
    private PartnerModel owner;

    @OneToOne(mappedBy = "game")
    @JsonIgnore
    private GameLoanModel gameLoanModel;

}
