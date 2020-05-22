package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.PoliticianEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PoliticianRepository extends JpaRepository<PoliticianEntity, Long> {
    Optional<PoliticianEntity> findByCitizenID(CitizenEntity CitizenID);
    PoliticianEntity findByPoliticianID(Long PoliticianID);
    Optional<PoliticianEntity> findByCitizenID_Pesel(String pesel);
    Optional<PoliticianEntity> findByCitizenID_Email(String email);
    Optional<PoliticianEntity> findByCitizenID_IdNumber(String idNumber);
    List<PoliticianEntity> findAll();


}
