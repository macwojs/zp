package edu.agh.zp.repositories;

import edu.agh.zp.objects.CitizenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CitizenRepository extends JpaRepository<CitizenEntity, Long> {
    CitizenEntity findByCitizenID(Long CitizenID);
    Optional<CitizenEntity> findByPesel(String pesel);
    Optional<CitizenEntity> findByEmail(String email);
    Optional<CitizenEntity> findByIdNumber(String idNumber);

    long count();

    @Transactional
    @Modifying
    @Query(value="UPDATE citizen SET town=?2, adress=?3 where citizenid =?1", nativeQuery=true)
    void updateAddress(long id, String f1, String f2);


    @Transactional
    @Modifying
    @Query(value="UPDATE citizen SET email=?2 where citizenid =?1", nativeQuery=true)
    void updateEmail(long id, String f1);

    @Transactional
    @Modifying
    @Query(value="UPDATE citizen SET password=?2 where citizenid =?1", nativeQuery=true)
    void updatePass(long id, String f1);

}
