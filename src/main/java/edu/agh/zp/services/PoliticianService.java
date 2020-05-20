package edu.agh.zp.services;

import edu.agh.zp.objects.PoliticianEntity;
import edu.agh.zp.repositories.PoliticianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
@Service
public class PoliticianService {
    @Autowired
    private PoliticianRepository pR;

    public PoliticianEntity create(PoliticianEntity politician){
        return pR.save(politician);
    }

    public ArrayList<PoliticianEntity> findAll(){
        return (ArrayList<PoliticianEntity>)pR.findAll();
    }

    public Optional<PoliticianEntity> findByCitizenID(Long citizenId){
        return pR.findByCitizenID(citizenId);
    }

    public Optional<PoliticianEntity> findByEmail(String email){ return pR.findByCitizenID_Email(email);}

    public PoliticianEntity update( PoliticianEntity citizen ){
        return pR.save(citizen);
    }

    public Optional<PoliticianEntity> findByPesel(String pesel) { return pR.findByCitizenID_Pesel(pesel);}

    public PoliticianEntity findByPoliticianID(Long politicianID) { return pR.findByPoliticianID(politicianID);}

    public Optional<PoliticianEntity> findByIdNumer(String idNumber) { return pR.findByCitizenID_IdNumber(idNumber);}
}
