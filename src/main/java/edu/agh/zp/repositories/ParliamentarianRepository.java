package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.ParliamentarianEntity;
import edu.agh.zp.objects.PoliticianEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParliamentarianRepository extends JpaRepository<ParliamentarianEntity, Long> {
    Optional<ParliamentarianEntity> findByPoliticianID_CitizenID(CitizenEntity CitizenID);
    ParliamentarianEntity findByPoliticianID(PoliticianEntity PoliticianID);
    Optional<ParliamentarianEntity> findByPoliticianID_CitizenID_Pesel(String pesel);
    Optional<ParliamentarianEntity> findByPoliticianID_CitizenID_Email(String email);
    Optional<ParliamentarianEntity> findByPoliticianID_CitizenID_IdNumber(String idNumber);
    Optional<ParliamentarianEntity> findByParliamentarianID(Long parliamentarianID);
    Optional<ParliamentarianEntity> findByIdCardNumber(String cardID);

    List<ParliamentarianEntity> findAllByChamberOfDeputies(String chamberOfDeputies);
    List<ParliamentarianEntity> findAllByChamberOfDeputiesAndPoliticalGroup(String chamberOfDeputies, String politicalGroup);
    List<ParliamentarianEntity> findAllByPoliticalGroup(String politicalGroup);
    List<ParliamentarianEntity> findAll();

    long countAllByChamberOfDeputies(String chamberOfDeputies);
    long countAllByChamberOfDeputiesAndPoliticalGroup(String chamberOfDeputies, String politicalGroup);

}
