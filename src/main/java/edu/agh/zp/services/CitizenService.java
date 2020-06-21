package edu.agh.zp.services;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.repositories.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    public Optional<CitizenEntity> create(CitizenEntity citizen){
        return Optional.of( citizenRepository.save(citizen));
    }

    public ArrayList<CitizenEntity> findAll(){
        return (ArrayList<CitizenEntity>) citizenRepository.findAll();
    }

    public CitizenEntity findById(Long id){
        return citizenRepository.findByCitizenID(id);
    }

    public Optional<CitizenEntity> findByEmail(String email){ return citizenRepository.findByEmail(email);}

    public CitizenEntity update( CitizenEntity citizen ){
        return citizenRepository.save(citizen);
    }

    public Optional<CitizenEntity> findByPesel(String pesel) { return citizenRepository.findByPesel(pesel);}

    public Optional<CitizenEntity> findByIdNumer(String idNumber) { return citizenRepository.findByIdNumber(idNumber);}

    public long countEntitledToVote(){
        return citizenRepository.count();
    }
}