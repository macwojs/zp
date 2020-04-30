package edu.agh.zp.hibernate;

import edu.agh.zp.objects.CitizenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {
    CitizenEntity findByCitizenID(Long CitizenID);
    CitizenEntity findByPesel(String Pesel);

}
