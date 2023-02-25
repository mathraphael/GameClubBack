package br.ufms.cpcx.api.gamersclub.services;

import br.ufms.cpcx.api.gamersclub.dtos.GameLoanDto;
import br.ufms.cpcx.api.gamersclub.models.GameLoanModel;
import br.ufms.cpcx.api.gamersclub.models.GameModel;
import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import br.ufms.cpcx.api.gamersclub.repositories.GameLoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class GameLoanService {


    private GameLoanRepository gameLoanRepository;


    private GameService gameService;


    private PartnerService partnerService;


    //para criar apenas uma instancia dessas 3 variaveis
    // da pra fazer com lombok
    @Autowired
    public GameLoanService(GameLoanRepository gameLoanRepository, GameService gameService, PartnerService partnerService) {
        this.gameLoanRepository = gameLoanRepository;
        this.gameService = gameService;
        this.partnerService = partnerService;
    }


    //metodo que salva a locacao
    @Transactional
    public GameLoanModel saveLoan(GameLoanDto gameLoanDto) {

        final int MAX_LOAN = 5;

        //busca no banco
        //se retornar um game model daria erro se o jogo nao existir
        Optional<GameModel> byIdGame = this.gameService.findById(gameLoanDto.getIdGame());
        Optional<PartnerModel> byIdPartner = this.partnerService.findById(gameLoanDto.getIdPartner());


        //tratamento de erro, para quando nao encontrar o jogo ou o partner
        if (byIdGame.isEmpty() || byIdPartner.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game or Partner not found!");
        }

        List<GameLoanModel> allLoan = this.gameLoanRepository.findAllByPartnerId(byIdPartner.get().getId());

        if (allLoan.size() >= MAX_LOAN) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Maximum loan reached!");

        }
        //verificar se o jogo esta atrasado
        if (isLoanDelay(allLoan)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is an overdue loan!");
        }
        GameLoanModel gameLoanModel = new GameLoanModel();
        GameLoanModel.builder()
                .game(byIdGame.get())
                .partner(byIdPartner.get())
                .loanDate(LocalDate.now())
                .returnDate(LocalDate.now().plusDays(7))
                .scheduledReturnDate(null).build();
        return this.gameLoanRepository.save(gameLoanModel);
    }

    private boolean isLoanDelay(List<GameLoanModel> gameLoanModelList) {
        for (var loan : gameLoanModelList) {
            //verifica se a data atual eh posterior a data que foi combinada
            if (LocalDate.now().isAfter(loan.getReturnDate())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkLoan(Long id) {
        return this.gameLoanRepository.existsByPartnerId(id);
    }
}
