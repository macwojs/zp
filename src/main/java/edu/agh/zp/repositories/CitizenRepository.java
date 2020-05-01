package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {
    CitizenEntity findByCitizenID(Long CitizenID);
    CitizenEntity findByPesel(String Pesel);

}
