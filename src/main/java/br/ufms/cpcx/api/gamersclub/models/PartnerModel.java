package br.ufms.cpcx.api.gamersclub.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name= "TB_PARTNER",
        uniqueConstraints = { @UniqueConstraint(name = "TB_PARTNER_UQ" , columnNames = {
                "name", "phoneNumber" })
        })
@Data
@NoArgsConstructor
public class PartnerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @JsonIgnoreProperties("owner")
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    // Nota: Evite orphanRemoval = true, pois executara um delete para cada entidade vinculada, 100 entidades = 100 deletes.
    // Se for deixar automatico use OnDelete Cascade action
    private List<GameModel> games;

    @OneToOne(mappedBy = "partner")
    @JsonIgnore
    private GameLoanModel gameLoanModel;


    public void setGames(List<GameModel> items){
        this.games = new ArrayList<>(items);
    }
}
