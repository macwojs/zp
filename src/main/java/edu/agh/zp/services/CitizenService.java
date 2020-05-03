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
    private CitizenRepository cR;

    public CitizenEntity create(CitizenEntity citizen){
        return cR.save(citizen);
    }

    public ArrayList<CitizenEntity> findAll(){
        return (ArrayList<CitizenEntity>)cR.findAll();
    }

    public CitizenEntity findById(Long id){
        return cR.findByCitizenID(id);
    }

    public Optional<CitizenEntity> findByEmail(String email){ return cR.findByEmail(email);}

    public CitizenEntity update( CitizenEntity citizen ){
        return cR.save(citizen);
    }
 }