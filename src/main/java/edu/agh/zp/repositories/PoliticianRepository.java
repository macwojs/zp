package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.PoliticianEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PoliticianRepository extends JpaRepository<PoliticianEntity, Long> {
    Optional<PoliticianEntity> findByCitizenID(Long CitizenID);
    Optional<PoliticianEntity> findByPoliticianID(Long PolitiacianID);
    Optional<PoliticianEntity> findByPesel(String pesel);
    Optional<PoliticianEntity> findByEmail(String email);
    Optional<PoliticianEntity> findByIdNumber(String idNumber);
    List<PoliticianEntity> findAll();
}
