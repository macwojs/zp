package edu.agh.zp.services;

import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.PoliticianEntity;
import edu.agh.zp.repositories.CitizenRepository;
import edu.agh.zp.repositories.PoliticianRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;

public class PoliticianService {
    @Autowired
    private PoliticianRepository pR;

    public PoliticianEntity create(PoliticianEntity politician){
        return pR.save(politician);
    }

    public ArrayList<PoliticianEntity> findAll(){
        return (ArrayList<PoliticianEntity>)pR.findAll();
    }

    public Optional<PoliticianEntity> findById(Long id){
        return pR.findByCitizenID(id);
    }

    public Optional<PoliticianEntity> findByEmail(String email){ return pR.findByEmail(email);}

    public PoliticianEntity update( PoliticianEntity citizen ){
        return pR.save(citizen);
    }

    public Optional<PoliticianEntity> findByPesel(String pesel) { return pR.findByPesel(pesel);}

    public Optional<PoliticianEntity> findByPoliticianID(Long politicianID) { return pR.findByPoliticianID(politicianID);}

    public Optional<PoliticianEntity> findByIdNumer(String idNumber) { return pR.findByIdNumber(idNumber);}
}
