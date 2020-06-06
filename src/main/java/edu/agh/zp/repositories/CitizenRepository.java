package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {
    CitizenEntity findByCitizenID(Long CitizenID);
    Optional<CitizenEntity> findByPesel(String pesel);
    Optional<CitizenEntity> findByEmail(String email);
    Optional<CitizenEntity> findByIdNumber(String idNumber);

    long count();

}
