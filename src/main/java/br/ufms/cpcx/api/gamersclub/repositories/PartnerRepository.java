package br.ufms.cpcx.api.gamersclub.repositories;

import br.ufms.cpcx.api.gamersclub.models.PartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PartnerRepository extends JpaRepository <PartnerModel, Long> {
    boolean existsByNameAndPhoneNumber(String name, String phoneNumber);
    boolean existsByGamesId(Long id);

}
